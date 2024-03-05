package com.quote.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import com.quote.dto.QuoteDto;
import com.quote.dto.QuoteDtoWrapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@EnableWireMock({
      @ConfigureWireMock(port = 7777, name = "external-service", property = "http://localhost")
})
@SpringBootTest
class ExternalQuoteServiceImplTest {

   @InjectWireMock("external-service")
   private WireMockServer wiremock;

   private final String baseUrl = "http://localhost:7777";

   RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();

   private final ExternalQuoteServiceImpl externalQuoteService = new ExternalQuoteServiceImpl(restClient);

   @BeforeEach
   void setUp() {
      ReflectionTestUtils.setField(externalQuoteService, "randomQuotesUri", "/random");
      ReflectionTestUtils.setField(externalQuoteService, "defaultPageSize", 20);
   }

   @Test
   void testFindQuoteById() {
      QuoteDto quoteDto = getQuoteDto();
      wiremock.stubFor(get("/1").willReturn(okJson(getQuoteDtoString())));
      QuoteDto quoteById = externalQuoteService.findQuoteById("1");

      assertEquals(quoteDto, quoteById);
   }

   @Test
   void testGetRandomQuotes() {
      List<QuoteDto> quoteDto = List.of(getQuoteDto());
      wiremock.stubFor(get("/random").willReturn(okJson(getRandomQuoteDtoString())));
      List<QuoteDto> randomQuotes = externalQuoteService.getRandomQuotes();

      assertEquals(quoteDto, randomQuotes);
   }

   @Test
   void testFindQuotesByFilter() {
      List<QuoteDto> quoteDto = List.of(getQuoteDto());
      wiremock.stubFor(get("/?author=author&tags=tags&limit=20").willReturn(okJson(getRandomQuoteWrapperDtoString())));
      List<QuoteDto> randomQuotes = externalQuoteService.findQuotesByFilter("author", "tags", 20).getResults();

      assertEquals(quoteDto, randomQuotes);
   }

   private QuoteDto getQuoteDto() {
      QuoteDto quoteDto = new QuoteDto();
      quoteDto.setAuthor("Some Author");
      quoteDto.setContent("Quote Content");
      quoteDto.setId("someId");
      quoteDto.setTags(Set.of("tag1", "tag2"));
      quoteDto.setAuthorSlug("some-author");

      return quoteDto;
   }

   private String getQuoteDtoString() {
      ObjectMapper objectMapper = new ObjectMapper();

      try {
         return objectMapper.writeValueAsString(getQuoteDto());
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }

   private String getRandomQuoteDtoString() {
      ObjectMapper objectMapper = new ObjectMapper();

      try {
         return objectMapper.writeValueAsString(List.of(getQuoteDto()));
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }

   private String getRandomQuoteWrapperDtoString() {
      ObjectMapper objectMapper = new ObjectMapper();

      QuoteDtoWrapper quoteDtoWrapper = new QuoteDtoWrapper();
      quoteDtoWrapper.setResults(List.of(getQuoteDto()));

      try {
         return objectMapper.writeValueAsString(quoteDtoWrapper);
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }
}