package com.quote.service.impl;

import com.quote.dto.QuoteDto;
import com.quote.entity.QuoteRate;
import com.quote.repository.QuoteRateRepository;
import com.quote.repository.QuoteRepository;
import com.quote.service.RateManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateManagerImpl implements RateManager {

   private final QuoteRateRepository quoteRateRepository;
   private final QuoteRepository quoteRepository;

   private final Random random = new Random();

   @Override
   public void incrementQuoteRate(String quoteId) {
      log.info("Rate Quote Id: {}", quoteId);
      QuoteRate quoteRate = quoteRateRepository.findById(quoteId)
            .orElseGet(() -> QuoteRate.builder()
                  .quoteId(quoteId)
                  .rate(0L)
                  .build());

      quoteRate.setRate(quoteRate.getRate() + 1);

      quoteRateRepository.save(quoteRate);
   }

   @Override
   public void fillQuoteRate() {
      log.info("Filling rate data for testing purposes");
      List<QuoteRate> list = quoteRepository.getAllQuotes().stream()
            .map(QuoteDto::getId)
            .map(id -> QuoteRate.builder().quoteId(id).rate(random.nextLong(100L)).build())
            .toList();

      quoteRateRepository.saveAll(list);
   }

   @Override
   public void appendRateData(QuoteDto quoteDto) {
      log.info("Getting quote rate data for Quote ID: {}" , quoteDto.getId());
      Optional<QuoteRate> rate = quoteRateRepository.findById(quoteDto.getId());
      if (rate.isPresent()) {
         quoteDto.setRate(rate.get().getRate());
      } else {
         quoteDto.setRate(0L);
      }
   }

   @Override
   public void appendRateData(List<QuoteDto> quoteDtoList) {
      log.info("Getting quote rate data for Quotes size {}" , quoteDtoList.size());
      Set<String> quoteIds = quoteDtoList.stream().map(QuoteDto::getId).collect(Collectors.toSet());
      Map<String, QuoteRate> rateMap = quoteRateRepository.findAllByQuoteIdIn(quoteIds).stream()
            .collect(Collectors.toMap(QuoteRate::getQuoteId, Function.identity()));

      quoteDtoList.forEach(quoteDto -> {
         QuoteRate rate = rateMap.get(quoteDto.getId());
         if (Objects.nonNull(rate)) {
            quoteDto.setRate(rate.getRate());
         } else {
            quoteDto.setRate(0L);
         }
      });
   }

   @Override
   public Optional<String> getRandomQuoteRateFromTop50() {
      List<QuoteRate> top50Rate = quoteRateRepository.findTop50ByOrderByRateDesc();
      if (CollectionUtils.isEmpty(top50Rate)) {
         return Optional.empty();
      } else {
         int i = random.nextInt(top50Rate.size());
         return Optional.of(top50Rate.get(i).getQuoteId());
      }
   }
}
