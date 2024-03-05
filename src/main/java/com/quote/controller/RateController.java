package com.quote.controller;

import com.quote.service.RateManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Profile("local")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rate")
public class RateController {

   private final RateManager rateManager;


   @Operation(summary = "Fill In Memory table with Rate data (for testing purposes only, enabled only for local env)")
   @PostMapping
   public ResponseEntity<Void> fillQuoteRate() {
      rateManager.fillQuoteRate();

      return ResponseEntity.ok().build();
   }
}
