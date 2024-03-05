package com.quote.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteDtoWrapper {
   private Integer count;
   private Integer totalCount;
   private Integer page;
   private Integer totalPages;
   private Integer lastItemIndex;
   private List<QuoteDto> results;

}
