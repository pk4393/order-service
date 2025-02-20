package com.example.order_service.controller;

import java.time.LocalDate;
import java.util.List;

import com.example.order_service.request.createorder.CreateOrderRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.OrderResponse;
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
  public ResponseEntity<String> createOrder(
          @RequestParam Integer userId,
          @RequestBody CreateOrderRequest createOrderRequest) {
    return ResponseEntity.ok(orderService.createOrder(userId, createOrderRequest));
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
  public ResponseEntity<BaseResponse<List<OrderResponse>>> getOrders(
      @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
    return ResponseEntity.ok(orderService.getOrders(page, size));
  }
}
