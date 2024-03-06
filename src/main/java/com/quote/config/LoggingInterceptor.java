package com.quote.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

   @Override
   public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
      logRequest(request, body);
      ClientHttpResponse response = execution.execute(request, body);
      logResponse(response);
      return response;
   }

   private void logRequest(HttpRequest request, byte[] body) {
      if (log.isDebugEnabled()) {
         log.debug("=========================== request begin =====================================");
         log.debug("URI         : {}", request.getURI());
         log.debug("Method      : {}", request.getMethod());
         log.debug("Headers     : {}", request.getHeaders());
         log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
         log.debug("===========================request end =====================================");
      }
   }

   private void logResponse(ClientHttpResponse response) throws IOException {
      if (log.isDebugEnabled()) {
         log.debug("=========================== response begin =====================================");
         log.debug("Status code  : {}", response.getStatusCode());
         log.debug("Status text  : {}", response.getStatusText());
         log.debug("Headers      : {}", response.getHeaders());
         InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
         String body = new BufferedReader(isr)
               .lines()
               .collect(Collectors.joining("\n"));
         log.debug("Response body: {}", body);
         log.debug("=========================== response end ======================================");
         log.debug("");
      }
   }
}
