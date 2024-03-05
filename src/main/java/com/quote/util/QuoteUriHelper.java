package com.quote.util;

import com.quote.dto.QuoteQueryParams;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuoteUriHelper {

   public static String buildUri(String uri, Map<QuoteQueryParams, String> queryParams) {
      return queryParams.entrySet().stream()
            .filter(entry -> !Objects.isNull(entry.getValue()))
            .map(entry -> entry.getKey().getValue() + "=" + entry.getValue())
            .collect(Collectors.joining("&", uri + "?", ""));
   }
}
