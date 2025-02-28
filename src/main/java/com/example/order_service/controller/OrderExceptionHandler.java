package com.example.order_service.controller;

import com.example.order_service.exception.CreateOrderException;
import com.example.order_service.exception.LimitedStockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.model.exception.MissingHeaderException;
import com.example.order_service.model.exception.ProductNotFoundException;
import com.example.order_service.response.BaseResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

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
  public ResponseEntity<BaseResponse<String>> handleCreateOrderException(CreateOrderException ex) {
    log.error("CreateOrderException: '{}'", ex.getMessage());

    BaseResponse<String> baseResponse = BaseResponse.<String>builder()
            .errorMessage(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST.name())
            .build();

    return ResponseEntity.badRequest().body(baseResponse);
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

  @ExceptionHandler(LimitedStockException.class)
  public ResponseEntity<BaseResponse<String>> handleLimitedStockException(
          LimitedStockException ex) {
    log.error(ex.getMessage(), ex);

    BaseResponse<String> baseResponse = BaseResponse.<String>builder()
            .errorMessage(ex.getMessage()) // Pass exception message
            .status(HttpStatus.BAD_REQUEST.name())
            .build();

    return ResponseEntity.badRequest().body(baseResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.error("MethodArgumentNotValidException: '{}'", ex.getMessage());

    String errorMessage = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

    BaseResponse<String> baseResponse = BaseResponse.<String>builder()
            .errorMessage(errorMessage)
            .status(HttpStatus.BAD_REQUEST.name())
            .build();

    return ResponseEntity.badRequest().body(baseResponse);
  }


  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<BaseResponse<String>> handleConstraintViolationException(ConstraintViolationException ex) {
    log.error("ConstraintViolationException: '{}'", ex.getMessage());

    String errorMessage = ex.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

    BaseResponse<String> baseResponse = BaseResponse.<String>builder()
            .errorMessage(errorMessage)
            .status(HttpStatus.BAD_REQUEST.name())
            .build();

    return ResponseEntity.badRequest().body(baseResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatchException(
          MethodArgumentTypeMismatchException ex) {
    log.error("Invalid parameter: '{}'", ex.getMessage());

    BaseResponse<String> baseResponse = BaseResponse.<String>builder()
            .errorMessage("Invalid value for parameter '" + ex.getName() + "'. Expected an integer.")
            .status(HttpStatus.BAD_REQUEST.name())
            .build();

    return ResponseEntity.badRequest().body(baseResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    log.error("Invalid request body: {}", ex.getMessage());

    BaseResponse<String> response = BaseResponse.<String>builder()
            .status(HttpStatus.BAD_REQUEST.name())
            .errorMessage("Invalid request body format. Please check your JSON syntax.")
            .build();

    return ResponseEntity.badRequest().body(response);
  }


}
