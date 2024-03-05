package com.quote.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quote.dto.QuoteDto;
import com.quote.dto.QuoteDtoWrapper;
import com.quote.entity.QuoteRate;
import com.quote.repository.QuoteRepository;
import com.quote.service.ExternalQuoteService;
import com.quote.service.RateManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuoteManagerImplTest {

   @Mock
   private RateManager rateManager;
   @Mock
   private QuoteRepository quoteRepository;
   @Mock
   private ExternalQuoteService externalQuoteService;

   @InjectMocks
   private QuoteManagerImpl quoteManager;



   @Test
   void getRandomQuote_should_return_prioritized_quote() {
      Optional<String> quoteId = Optional.of("someId");
      QuoteDto quoteDto = getQuoteDto();

      when(rateManager.getRandomQuoteRateFromTop50()).thenReturn(quoteId);
      when(externalQuoteService.findQuoteById(quoteId.get())).thenReturn(quoteDto);

      Pair<QuoteDto, String> actual = quoteManager.getRandomQuote(true);

      assertEquals("Success", actual.getRight());
      assertEquals(quoteDto, actual.getLeft());
   }

   @Test
   void getRandomQuote_should_return_random_when_prioritized_quote_not_found() {
      Optional<String> quoteId = Optional.empty();
      QuoteDto quoteDto = getQuoteDto();

      when(rateManager.getRandomQuoteRateFromTop50()).thenReturn(quoteId);
      when(externalQuoteService.getRandomQuotes()).thenReturn(List.of(quoteDto));
      Pair<QuoteDto, String> actual = quoteManager.getRandomQuote(true);

      assertEquals("Success", actual.getRight());
      assertEquals(quoteDto, actual.getLeft());
   }

   @Test
   void getRandomQuote_should_return_random_quote() {
      QuoteDto quoteDto = getQuoteDto();

      when(externalQuoteService.getRandomQuotes()).thenReturn(List.of(quoteDto));
      Pair<QuoteDto, String> actual = quoteManager.getRandomQuote(false);

      assertEquals("Success", actual.getRight());
      assertEquals(quoteDto, actual.getLeft());
   }

   @Test
   void getRandomQuote_should_return_random_quote_from_local_resource() {
      QuoteDto quoteDto = getQuoteDto();

      when(quoteRepository.getAllQuotes()).thenReturn(List.of(quoteDto));
      Pair<QuoteDto, String> actual = quoteManager.getRandomQuote(false);

      assertEquals("Alternative Quote", actual.getRight());
      assertEquals(quoteDto, actual.getLeft());
   }

   @Test
   void rateQuote_should_call_rate_manager() {
      String quoteId = "quoteId";

      quoteManager.rateQuote(quoteId);

      verify(rateManager, times(1)).incrementQuoteRate(quoteId);
   }

   @Test
   void findQuotes_should_return_quote_from_external_service() {
      var author = "author";
      var tags = "tag";
      var limit = 10;
      QuoteDtoWrapper quoteDtoWrapper = getQuoteDtoWrapper();

      when(externalQuoteService.findQuotesByFilter(author, tags, limit)).thenReturn(quoteDtoWrapper);
      Pair<List<QuoteDto>, String> actual = quoteManager.findQuotes(author, tags, limit);

      assertEquals("Success", actual.getRight());
      assertEquals(quoteDtoWrapper.getResults(), actual.getLeft());
   }

   @Test
   void findQuotes_should_not_any_return_when_not_find_by_filter() {
      var author = "author";
      var tags = "tag";
      var limit = 10;

      Pair<List<QuoteDto>, String> actual = quoteManager.findQuotes(author, tags, limit);

      assertEquals("Quotes are not found by specified filter", actual.getRight());
      assertEquals(0, actual.getLeft().size());
   }

   private QuoteDto getQuoteDto() {
      QuoteDto quoteDto = new QuoteDto();
      quoteDto.setAuthor("Some Author");
      quoteDto.setContent("Quote Content");
      quoteDto.setId("someId");
      quoteDto.setTags(Set.of("tag1", "tag2"));
      quoteDto.setAuthorSlug("some-author");

      return quoteDto;
   }

   private QuoteDtoWrapper getQuoteDtoWrapper() {
      QuoteDtoWrapper quoteDtoWrapper = new QuoteDtoWrapper();
      quoteDtoWrapper.setResults(List.of(getQuoteDto()));

      return quoteDtoWrapper;
   }
}