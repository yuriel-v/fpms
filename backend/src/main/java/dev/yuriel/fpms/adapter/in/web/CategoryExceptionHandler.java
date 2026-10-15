package dev.yuriel.fpms.adapter.in.web;

import dev.yuriel.fpms.adapter.in.web.dto.ErrorResponse;
import dev.yuriel.fpms.application.port.in.CategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates category application-layer exceptions into consistent HTTP error responses at the web
 * edge. A missing or not-owned category is reported as {@code 404 Not Found} (the two are
 * indistinguishable by design, so a user cannot probe for others' category ids).
 */
@RestControllerAdvice
public class CategoryExceptionHandler {

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
  }
}
