package com.example.order_service.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderReportRequestException extends RuntimeException {
  public InvalidOrderReportRequestException(String message) {
    super(message);
  }
}