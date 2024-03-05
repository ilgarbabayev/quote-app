package com.quote.dto;

import lombok.Getter;

@Getter
public enum QuoteQueryParams {

   AUTHOR("author"),
   TAGS("tags"),
   LIMIT("limit");

   private final String value;

   QuoteQueryParams(String value) {
      this.value = value;
   }
}
