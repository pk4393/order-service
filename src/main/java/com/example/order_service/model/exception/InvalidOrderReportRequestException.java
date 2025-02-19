package com.example.order_service.model.exception;

public class InvalidOrderReportRequestException extends RuntimeException {
  public InvalidOrderReportRequestException(String message) {
    super(message);
  }
}