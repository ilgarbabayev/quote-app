package com.quote.service;

import com.quote.dto.QuoteDto;
import java.util.List;
import java.util.Optional;

public interface RateManager {

   void incrementQuoteRate(String quoteId);

   void fillQuoteRate();

   QuoteDto appendRateData(QuoteDto quoteDto);

   List<QuoteDto> appendRateData(List<QuoteDto> quoteDtoList);

   Optional<String> getRandomQuoteRateFromTop50();
}
