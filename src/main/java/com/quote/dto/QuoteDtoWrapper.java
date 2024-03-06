package com.quote.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QuoteDtoWrapper {
   Integer count;
   Integer totalCount;
   Integer page;
   Integer totalPages;
   Integer lastItemIndex;
   List<QuoteDto> results;

}
