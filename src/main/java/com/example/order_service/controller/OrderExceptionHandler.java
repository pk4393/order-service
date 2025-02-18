package com.example.order_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception e) {
    return ResponseEntity.internalServerError().build();
  }
}
