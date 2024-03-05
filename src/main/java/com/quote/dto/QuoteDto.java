package com.quote.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.Set;
import lombok.Data;

@Data

public class QuoteDto {

   @JsonAlias("_id")
   private String id;
   private String content;
   private String author;
   private String authorSlug;
   private Set<String> tags;
   private Long rate;

}
