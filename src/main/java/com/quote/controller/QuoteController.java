package com.quote.controller;

import com.quote.dto.QuoteDto;
import com.quote.dto.ResponseHandler;
import com.quote.service.QuoteManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteController {

   private final QuoteManager quoteManager;

   @Operation(summary = "Get random Quote")
   @ApiResponses(value = {
         @ApiResponse(responseCode = "200", description = "Random Quote retrieved",
               content = { @Content(mediaType = "application/json",
                     schema = @Schema(implementation = QuoteDto.class)) }),
         @ApiResponse(responseCode = "404", description = "Can not get Quote",
               content = @Content) })
   @GetMapping(value = "/random")
   public ResponseEntity<Object> getRandomQuote(
         @Parameter(description = "Get high ranking Quote for prioritized user")
         @RequestParam(required = false, defaultValue = "false")
         Boolean hasPriority) {

      Pair<QuoteDto, String> randomQuote = quoteManager.getRandomQuote(hasPriority);

      return ResponseHandler.generateResponse(randomQuote.getRight(), HttpStatus.OK, randomQuote.getLeft());
   }

   @Operation(summary = "Find Quote by filter")
   @ApiResponses(value = {
         @ApiResponse(responseCode = "200", description = "Quotes retrieved",
               content = { @Content(mediaType = "application/json",
                     array = @ArraySchema(schema = @Schema(implementation = QuoteDto.class))) }),
         @ApiResponse(responseCode = "404", description = "Can not find Quotes",
               content = @Content) })
   @GetMapping
   public ResponseEntity<Object> findQuotes(
         @Parameter(description = "Filter Quotes by authors")
         @RequestParam(required = false)
         String author,
         @Parameter(description = "Filter Quotes by tags")
         @RequestParam(required = false)
         String tags,
         @Parameter(description = "Limit result size")
         @RequestParam(required = false)
         Integer limit) {
      Pair<List<QuoteDto>, String> quotes = quoteManager.findQuotes(author, tags, limit);

      return ResponseHandler.generateResponse(quotes.getRight(), HttpStatus.OK, quotes.getLeft());
   }

   @Operation(summary = "Rate Quote")
   @PostMapping(value = "/{quoteId}/rate")
   public ResponseEntity<Void> rateQuote(@PathVariable @Parameter(name = "quoteId", description = "Quote ID") String quoteId) {
      quoteManager.rateQuote(quoteId);
      return ResponseEntity.ok().build();
   }
}
