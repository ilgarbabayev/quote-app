package com.quote.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.Set;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class QuoteDto {

   @JsonAlias("_id")
   String id;
   String content;
   String author;
   String authorSlug;
   @Singular
   Set<String> tags;
   Long rate;

}
