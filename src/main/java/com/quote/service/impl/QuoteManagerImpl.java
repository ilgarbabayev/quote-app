package com.quote.service.impl;

import com.quote.dto.QuoteDto;
import com.quote.dto.QuoteDtoWrapper;
import com.quote.repository.QuoteRepository;
import com.quote.service.ExternalQuoteService;
import com.quote.service.QuoteManager;
import com.quote.service.RateManager;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteManagerImpl implements QuoteManager {

   public static final String EXTERNAL_RESOURCE_ERROR_MESSAGE = "Unable to retrieve Quotes from external resource";
   public static final String ALTERNATIVE_QUOTE_MESSAGE = "Alternative Quote";
   public static final String SUCCESS_MESSAGE = "Success";
   public static final String QUOTES_NOT_FOUND_MESSAGE = "Quotes are not found by specified filter";

   private final RateManager rateManager;
   private final QuoteRepository quoteRepository;
   private final ExternalQuoteService externalQuoteService;

   private final Random random = new Random();


   @Override
   @Retry(name = "extQuoteApi", fallbackMethod = "fallbackQuotes")
   public Pair<QuoteDto, String> getRandomQuote(boolean hasPriority) {
      if (BooleanUtils.isTrue(hasPriority)) {
         return getRandomPrioritisedQuote();
      } else {
         return getRandomQuote();
      }
   }

   @Override
   public Pair<List<QuoteDto>, String> findQuotes(String author, String tags, Integer limit) {

      log.info("Find Quotes By filters:");

      QuoteDtoWrapper response = externalQuoteService.findQuotesByFilter(author, tags, limit);

      if (Objects.nonNull(response) && !CollectionUtils.isEmpty(response.getResults())) {
         List<QuoteDto> results = response.getResults();
         rateManager.appendRateData(results);
         return Pair.of(results, SUCCESS_MESSAGE);
      } else {
         log.info(QUOTES_NOT_FOUND_MESSAGE);
         return Pair.of(new ArrayList<>(), QUOTES_NOT_FOUND_MESSAGE);
      }
   }

   @Override
   public void rateQuote(String quoteId) {
      rateManager.incrementQuoteRate(quoteId);
   }

   private Pair<QuoteDto, String> getRandomPrioritisedQuote() {
      Optional<String> priorityQuote = rateManager.getRandomQuoteRateFromTop50();

      if (priorityQuote.isPresent()) {
         log.info("Getting Prioritised Quote");
         String quoteId = priorityQuote.get();

         QuoteDto quoteDto = externalQuoteService.findQuoteById(quoteId);

         if (Objects.nonNull(quoteDto)) {
            rateManager.appendRateData(quoteDto);
            return Pair.of(quoteDto, SUCCESS_MESSAGE);
         }
      }
      return getRandomQuote();
   }

   private Pair<QuoteDto, String> getRandomQuote() {
      log.info("Getting Random Quote");

      String msg;
      QuoteDto quoteDto;

      List<QuoteDto> response = externalQuoteService.getRandomQuotes();

      if (CollectionUtils.isEmpty(response)) {
         log.info("Getting Fallback Quotes");
         quoteDto = getRandomQuoteFromLocalResource();
         msg = ALTERNATIVE_QUOTE_MESSAGE;
      } else {
         quoteDto = response.get(0);
         msg = SUCCESS_MESSAGE;
      }
      rateManager.appendRateData(quoteDto);

      return Pair.of(quoteDto, msg);
   }

   private Pair<QuoteDto, String> fallbackQuotes(Throwable e) {
      log.error(EXTERNAL_RESOURCE_ERROR_MESSAGE, e);
      log.info("Getting Quote from Local source");
      QuoteDto quoteDto = getRandomQuoteFromLocalResource();
      rateManager.appendRateData(quoteDto);
      return Pair.of(quoteDto, ALTERNATIVE_QUOTE_MESSAGE);
   }

   private QuoteDto getRandomQuoteFromLocalResource() {
      return getRandomQuoteFromrList(quoteRepository.getAllQuotes());
   }

   private QuoteDto getRandomQuoteFromrList(List<QuoteDto> quotes) {
      int i = random.nextInt(quotes.size());
      return quotes.get(i);
   }

}
