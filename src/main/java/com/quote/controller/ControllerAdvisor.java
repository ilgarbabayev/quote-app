package com.quote.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.quote.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler({HttpClientErrorException.class, RestClientException.class, ResourceNotFoundException.class})
  public final ResponseEntity<Object> handleHttpClientException(Exception ex) {
    log.error(ex.getMessage(), ex);
    HttpStatus status = HttpStatus.NOT_FOUND;
    ErrorResponse errorResponse = ErrorResponse.create(ex, status, ex.getMessage());
    return ResponseEntity.status(status)
                         .body(errorResponse);
  }

}
