package com.quote.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quote.dto.QuoteDto;
import com.quote.entity.QuoteRate;
import com.quote.repository.QuoteRateRepository;
import com.quote.repository.QuoteRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateManagerImplTest {

   @Mock
   private QuoteRateRepository quoteRateRepository;
   @Mock
   private QuoteRepository quoteRepository;

   @InjectMocks
   private RateManagerImpl rateManager;

   @Captor
   private ArgumentCaptor<List<QuoteRate>> rateCaptor;

   @Test
   void incrementQuoteRate_should_get_and_increment_rate() {
      String quoteId = "1";
      QuoteRate quoteRate = getQuoteRate();
      when(quoteRateRepository.findById(quoteId)).thenReturn(Optional.of(quoteRate));

      rateManager.incrementQuoteRate(quoteId);

      quoteRate.setRate(quoteRate.getRate() + 1);

      verify(quoteRateRepository, times(1)).save(quoteRate);

   }

   @Test
   void incrementQuoteRate_should_save_new_rate() {
      String quoteId = "1";
      QuoteRate quoteRate = getQuoteRate();

      when(quoteRateRepository.findById(quoteId)).thenReturn(Optional.empty());

      rateManager.incrementQuoteRate(quoteId);

      verify(quoteRateRepository, times(1)).save(quoteRate);

   }

   @Test
   void fillQuoteRate_should_add_rates() {

      List<QuoteDto> quoteDtos = getQuoteDtos();

      when(quoteRepository.getAllQuotes()).thenReturn(quoteDtos);

      rateManager.fillQuoteRate();

      verify(quoteRateRepository, times(1)).saveAll(rateCaptor.capture());

      List<QuoteRate> value = rateCaptor.getValue();

      assertEquals(3, value.size());
   }

   @Test
   void appendRateData_should_add_rate_to_quote_from_db() {
      QuoteDto quoteDto = new QuoteDto();
      quoteDto.setId("1");
      QuoteRate quoteRate = getQuoteRate();
      when(quoteRateRepository.findById(quoteDto.getId())).thenReturn(Optional.of(quoteRate));

      rateManager.appendRateData(quoteDto);

      assertEquals(quoteRate.getRate(), quoteDto.getRate());
   }

   @Test
   void appendRateData_should_set_rate_to_zero() {
      QuoteDto quoteDto = new QuoteDto();
      quoteDto.setId("1");

      rateManager.appendRateData(quoteDto);

      assertEquals(0L, quoteDto.getRate());
   }

   @Test
   void appendRateData_should_add_rates_to_quotes() {
      List<QuoteDto> quoteDtos = getQuoteDtos();
      Set<String> quoteIds = quoteDtos.stream().map(QuoteDto::getId).collect(Collectors.toSet());
      List<QuoteRate> quoteRates = getQuoteRates();

      when(quoteRateRepository.findAllByQuoteIdIn(quoteIds)).thenReturn(quoteRates);

      rateManager.appendRateData(quoteDtos);

      assertEquals(1L, quoteDtos.get(0).getRate());
      assertEquals(2L, quoteDtos.get(1).getRate());
      assertEquals(3L, quoteDtos.get(2).getRate());
   }

   @Test
   void getRandomQuoteRateFromTop50_should_return_random_rate_from_db() {
      List<QuoteRate> quoteRates = getQuoteRates();
      when(quoteRateRepository.findTop50ByOrderByRateDesc()).thenReturn(quoteRates);

      Optional<String> quoteId = rateManager.getRandomQuoteRateFromTop50();

      Optional<QuoteRate> first = quoteRates.stream().filter(rate -> rate.getQuoteId().equals(quoteId.get())).findFirst();

      assertTrue(first.isPresent());
   }


   private QuoteRate getQuoteRate() {
      return QuoteRate.builder().quoteId("1").rate(1L).build();
   }

   private List<QuoteRate> getQuoteRates() {
      return List.of(QuoteRate.builder().quoteId("1").rate(1L).build(),
            QuoteRate.builder().quoteId("2").rate(2L).build(),
            QuoteRate.builder().quoteId("3").rate(3L).build());
   }


   private List<QuoteDto> getQuoteDtos() {
      QuoteDto quoteDto1 = new QuoteDto();
      quoteDto1.setId("1");
      QuoteDto quoteDto2 = new QuoteDto();
      quoteDto2.setId("2");
      QuoteDto quoteDto3 = new QuoteDto();
      quoteDto3.setId("3");

      return List.of(quoteDto1, quoteDto2, quoteDto3);
   }

}