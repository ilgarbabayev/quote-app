package com.quote.service;

import com.quote.dto.QuoteDto;
import java.util.List;
import java.util.Optional;

public interface RateManager {

   void incrementQuoteRate(String quoteId);

   void fillQuoteRate();

   void appendRateData(QuoteDto quoteDto);

   void appendRateData(List<QuoteDto> quoteDtoList);

   Optional<String> getRandomQuoteRateFromTop50();
}
