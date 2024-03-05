package com.quote.repository;

import com.quote.dto.QuoteDto;
import java.util.List;

public interface QuoteRepository {

   List<QuoteDto> getAllQuotes();

}
