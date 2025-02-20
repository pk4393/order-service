package com.example.order_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.model.exception.MissingHeaderException;
import com.example.order_service.response.BaseResponse;

@RestControllerAdvice
public class OrderExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception e) {
    return ResponseEntity.internalServerError().body(BaseResponse.<String>builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name()).errorMessage(e.getMessage()).build());
  }

  @ExceptionHandler(InvalidOrderReportRequestException.class)
  public ResponseEntity<BaseResponse<String>> handleInvalidOrderReportRequestException(
      InvalidOrderReportRequestException e) {
    return ResponseEntity.badRequest().body(BaseResponse.<String>builder()
        .status(HttpStatus.BAD_REQUEST.name()).errorMessage(e.getMessage()).build());
  }

  @ExceptionHandler(MissingHeaderException.class)
  public ResponseEntity<BaseResponse<String>> handleMissingHeaderException(
      MissingHeaderException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponse.<String>builder()
        .status(HttpStatus.UNAUTHORIZED.name()).errorMessage(e.getMessage()).build());
  }
}
