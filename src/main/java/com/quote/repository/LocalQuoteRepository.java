package com.quote.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quote.dto.QuoteDto;
import com.quote.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LocalQuoteRepository implements QuoteRepository, ApplicationRunner {

   public static final String FALLBACK_QUOTES_FILE = "quotes/fallbackQuotes.json";
   public static final String LOCAL_RESOURCE_ERROR_MESSAGE = "Unable to retrieve Quotes from local resource";

   private final ObjectMapper mapper;
   private final List<QuoteDto> fallbackQuotes = new ArrayList<>();

   @Override
   public List<QuoteDto> getAllQuotes() {
      if (this.fallbackQuotes.isEmpty()) {
         try {
            InputStream inputStream = new ClassPathResource(FALLBACK_QUOTES_FILE).getInputStream();
            String json = FileCopyUtils.copyToString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            fallbackQuotes.addAll(mapper.readValue(json, new TypeReference<>() {
            }));
            return fallbackQuotes;
         } catch (IOException e) {
            log.error(LOCAL_RESOURCE_ERROR_MESSAGE);
            throw new ResourceNotFoundException(LOCAL_RESOURCE_ERROR_MESSAGE, e);
         }
      } else {
         return this.fallbackQuotes;
      }
   }

   @Override
   public void run(ApplicationArguments args) {
      try {
         InputStream inputStream = new ClassPathResource(FALLBACK_QUOTES_FILE).getInputStream();
         String json = FileCopyUtils.copyToString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
         fallbackQuotes.addAll(mapper.readValue(json, new TypeReference<>() {
         }));
      } catch (IOException e) {
         log.error(LOCAL_RESOURCE_ERROR_MESSAGE);
         throw new ResourceNotFoundException(LOCAL_RESOURCE_ERROR_MESSAGE, e);
      }
   }
}
