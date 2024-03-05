package com.quote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Value("${quotes.api.baseUrl}")
  private String baseUrl;

  @Bean
  public RestClient restClient(LoggingInterceptor interceptor) {
    return RestClient.builder()
                     .baseUrl(baseUrl)
                     .requestInterceptor(interceptor)
                     .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                     .build();
  }
}
