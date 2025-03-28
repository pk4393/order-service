package com.example.order_service.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MissingHeaderException extends RuntimeException {
  public MissingHeaderException(String message) {
    super(message);
  }
}