package com.quote.service.impl;

import com.quote.dto.QuoteDto;
import com.quote.dto.QuoteDtoWrapper;
import com.quote.dto.QuoteQueryParams;
import com.quote.exception.ResourceNotFoundException;
import com.quote.service.ExternalQuoteService;
import com.quote.util.QuoteUriHelper;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalQuoteServiceImpl implements ExternalQuoteService {

   private static final String EXTERNAL_RESOURCE_ERROR_MESSAGE = "Unable to retrieve Quotes from external resource";

   private final RestClient restClient;

   @Value("${quotes.api.defaultPageSize}")
   private Integer defaultPageSize;

   @Value("${quotes.api.path.randomQuotes}")
   private String randomQuotesUri;

   @Override
   public QuoteDtoWrapper findQuotesByFilter(String author, String tags, Integer limit) {
      log.info("Filters to find Quote authors: {}, tags: {}, limit: {}", author, tags, limit);

      if (Objects.isNull(limit)) {
         limit = defaultPageSize;
      }

      Map<QuoteQueryParams, String> params = new EnumMap<>(QuoteQueryParams.class);
      params.put(QuoteQueryParams.AUTHOR, author);
      params.put(QuoteQueryParams.TAGS, tags);
      params.put(QuoteQueryParams.LIMIT, limit.toString());
      String uri = QuoteUriHelper.buildUri(StringUtils.EMPTY, params);
      try {

         return restClient.get()
               .uri(uri)
               .accept(MediaType.APPLICATION_JSON)
               .retrieve()
               .body(new ParameterizedTypeReference<>() {
               });

      } catch (Exception e) {
         throw new ResourceNotFoundException(EXTERNAL_RESOURCE_ERROR_MESSAGE, e);
      }
   }

   @Override
   public QuoteDto findQuoteById(String quoteId) {
      try {
         return restClient.get()
               .uri("/{id}", quoteId)
               .accept(MediaType.APPLICATION_JSON)
               .retrieve()
               .body(QuoteDto.class);
      } catch (Exception e) {
         throw new ResourceNotFoundException(EXTERNAL_RESOURCE_ERROR_MESSAGE, e);
      }
   }

   @Override
   public List<QuoteDto> getRandomQuotes() {
      try {
         return restClient.get()
               .uri(randomQuotesUri)
               .accept(MediaType.APPLICATION_JSON)
               .retrieve()
               .body(new ParameterizedTypeReference<>() {
               });
      } catch (Exception e) {
         throw new ResourceNotFoundException(EXTERNAL_RESOURCE_ERROR_MESSAGE, e);
      }
   }
}
