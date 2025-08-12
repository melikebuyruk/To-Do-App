package com.example.demo.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import jakarta.validation.ConstraintViolationException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
      IllegalArgumentException.class,
      ServerWebInputException.class,
      ConstraintViolationException.class
  })
  public ProblemDetail handleBadRequest(Exception ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Bad Request");
    pd.setDetail(ex.getMessage());
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(ex.getStatusCode());
    pd.setTitle(ex.getReason() != null ? ex.getReason() : "Error");
    pd.setDetail(ex.getMessage());
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ProblemDetail handleErrorResponseException(ErrorResponseException ex) {
    ProblemDetail pd = ex.getBody();
    if (pd.getProperties() == null || !pd.getProperties().containsKey("timestamp")) {
      pd.setProperty("timestamp", Instant.now());
    }
    return pd;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleConflict(DataIntegrityViolationException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    pd.setTitle("Conflict");
    pd.setDetail("Data integrity violation: " + ex.getMostSpecificCause().getMessage());
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }

  /*@ExceptionHandler(Throwable.class)
  public ProblemDetail handleUnexpected(Throwable ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal Server Error");
    pd.setDetail("Unexpected error occurred");
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }
    */

}
