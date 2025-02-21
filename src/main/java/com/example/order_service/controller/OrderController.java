package com.example.order_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.request.createorder.CreateOrderRequest;
import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.ReportResponse;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;
  private final ObjectMapper objectMapper;

  @PostMapping("/order")
  public ResponseEntity<BaseResponse<String>> createOrder(
          @RequestParam Integer userId,
          @Validated @RequestBody CreateOrderRequest createOrderRequest) {

    BaseResponse<String> response = orderService.createOrder(userId, createOrderRequest);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/report")
  public ResponseEntity<BaseResponse<ReportResponse>> getOrderReport(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    if (productId == null && (startDate == null || endDate == null)) {
      throw new InvalidOrderReportRequestException(
          "Either productId or a date range (startDate & endDate) is required.");
    }
    return ResponseEntity.ok(orderService.getOrderReport(productId, startDate, endDate));
  }

  @GetMapping("/getOrders")
  public ResponseEntity<BaseResponse<List<Long>>> getOrders(
          @RequestParam(name = "page") int page,
          @RequestParam(name = "size") int size,
          @RequestParam(required = false) Long userId,
          @RequestParam(required = false) Long productId) {
    return ResponseEntity.ok(orderService.getOrders(page, size, userId, productId));
  }
}
