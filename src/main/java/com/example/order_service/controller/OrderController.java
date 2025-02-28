package com.example.order_service.controller;

import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.request.createorder.CreateOrderRequest;
import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.OrdersListingResponse;
import com.example.order_service.response.ReportResponse;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {
  private final OrderService orderService;
  private final ObjectMapper objectMapper;

  @PostMapping("/order")
  public ResponseEntity<BaseResponse<String>> createOrder(
          @RequestParam @Min(1) @NotNull Integer userId,
          @RequestBody @Valid CreateOrderRequest createOrderRequest) {

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
  public ResponseEntity<BaseResponse<OrdersListingResponse>> getOrders(
          @RequestParam(name = "page") int page,
          @RequestParam(name = "size") int size,
          @RequestParam(required = false) Long userId) {
    return ResponseEntity.ok(orderService.getOrders(page, size, userId));
  }
}
