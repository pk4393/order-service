package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.outbound.ProductApiClient;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.ReportResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final ObjectMapper objectMapper;
  private final ProductApiClient productApiClient;

  public BaseResponse<ReportResponse> getOrderReport(Long productId, LocalDate startDate,
      LocalDate endDate) {
    List<OrderEntity> orders;
    // Convert LocalDate to LocalDateTime
    LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
    LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
    if (productId == null && startDateTime == null && endDateTime == null) {
      throw new IllegalArgumentException(
          "Either productId or a date range (startDate & endDate) is required.");
    }
    if (productId != null && startDateTime != null && endDateTime != null) {
      orders =
          orderRepository.findOrdersByProductAndDateRange(productId, startDateTime, endDateTime);
    } else if (productId != null) {
      orders = orderRepository.findOrdersByProductId(productId);
    } else {
      orders = orderRepository.findOrdersByDateRange(startDateTime, endDateTime);
    }
    Long totalProducts =
        orders.stream()
            .map(orderEntity -> orderEntity.getOrderItems().stream()
                .map(OrderItemEntity::getProductId).distinct().count())
            .reduce(Long::sum).orElse(0L);
    Double totalRevenue =
        orders.stream().map(OrderEntity::getTotalPrice).reduce(Double::sum).orElse(0.0);
    ReportResponse reportResponse = ReportResponse.builder().totalOrders(orders.size())
        .totalProducts(totalProducts).totalRevenue(totalRevenue).build();
    return BaseResponse.<ReportResponse>builder().status(HttpStatus.OK.name())
        .data(reportResponse).build();
  }
}
