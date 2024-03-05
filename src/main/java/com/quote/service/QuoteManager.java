package com.quote.service;

import com.quote.dto.QuoteDto;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public interface QuoteManager {

   Pair<QuoteDto, String> getRandomQuote(boolean hasPriority);

   Pair<List<QuoteDto>, String> findQuotes(String author, String tags, Integer limit);

   void rateQuote(String quoteId);
}
