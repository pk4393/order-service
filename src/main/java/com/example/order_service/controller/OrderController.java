package com.example.order_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.OrderResponse;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;
  private final ObjectMapper objectMapper;

  @GetMapping("/report")
  public ResponseEntity<BaseResponse<List<OrderResponse>>> getOrderReport(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    if (productId == null && (startDate == null && endDate == null)) {
      throw new IllegalArgumentException(
          "Either productId or a date range (startDate & endDate) is required.");
    }
    return ResponseEntity.ok(orderService.getOrderReport(productId, startDate, endDate));
  }
}
