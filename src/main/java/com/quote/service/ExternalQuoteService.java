package com.quote.service;

import com.quote.dto.QuoteDto;
import com.quote.dto.QuoteDtoWrapper;
import java.util.List;

public interface ExternalQuoteService {

   QuoteDtoWrapper findQuotesByFilter(String author, String tags, Integer limit);

   QuoteDto findQuoteById(String id);

   List<QuoteDto> getRandomQuotes();
}
