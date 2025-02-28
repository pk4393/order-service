package com.example.order_service.controller;

import com.example.order_service.exception.CreateOrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.model.exception.MissingHeaderException;
import com.example.order_service.model.exception.ProductNotFoundException;
import com.example.order_service.response.BaseResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class OrderExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.internalServerError().body(BaseResponse.<String>builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name()).errorMessage(e.getMessage()).build());
  }

  @ExceptionHandler(InvalidOrderReportRequestException.class)
  public ResponseEntity<BaseResponse<String>> handleInvalidOrderReportRequestException(
      InvalidOrderReportRequestException e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.badRequest().body(BaseResponse.<String>builder()
        .status(HttpStatus.BAD_REQUEST.name()).errorMessage(e.getMessage()).build());
  }

  @ExceptionHandler(MissingHeaderException.class)
  public ResponseEntity<BaseResponse<String>> handleMissingHeaderException(
      MissingHeaderException e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponse.<String>builder()
        .status(HttpStatus.UNAUTHORIZED.name()).errorMessage(e.getMessage()).build());
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<BaseResponse<String>> handleProductNotFoundException(
      ProductNotFoundException e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.badRequest().body(BaseResponse.<String>builder()
        .status(HttpStatus.NOT_FOUND.name()).errorMessage(e.getMessage()).build());
  }

  @ExceptionHandler(CreateOrderException.class)
  public ResponseEntity<BaseResponse<String>> handleCreateOrderException(CreateOrderException e) {
    log.error(e.getMessage(), e);

    BaseResponse<String> response = BaseResponse.<String>builder()
            .status(HttpStatus.NOT_FOUND.name())
            .errorMessage(e.getMessage())
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(response);
  }
  
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<BaseResponse<String>> handleMissingRequestParameter(
      MissingServletRequestParameterException ex) {
    log.error(ex.getMessage(), ex);
    BaseResponse<String> baseResponse = BaseResponse.<String>builder()
        .errorMessage("Missing required request parameter: " + ex.getParameterName())
        .status(HttpStatus.BAD_REQUEST.name()).build();
    return ResponseEntity.badRequest().body(baseResponse);
  }
}
