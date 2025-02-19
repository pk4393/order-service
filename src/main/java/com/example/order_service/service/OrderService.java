package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.outbound.ProductApiClient;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.OrderItemResponse;
import com.example.order_service.response.OrderResponse;
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
      throw new InvalidOrderReportRequestException(
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

  public BaseResponse<List<OrderResponse>> getOrders(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<OrderEntity> orderPage = orderRepository.findAll(pageable);
    List<OrderEntity> orders = orderPage.getContent();
    List<OrderResponse> orderResponses = orders.stream().map(this::convertToResponse).toList();
    return BaseResponse.<List<OrderResponse>>builder().status(HttpStatus.OK.name())
        .data(orderResponses).build();
  }

  private OrderResponse convertToResponse(OrderEntity order) {
    List<OrderItemResponse> orderItemResponses =
        order.getOrderItems().stream().map(this::convertOrderItemToResponse).toList();
    return OrderResponse.builder().orderId(order.getOrderId()).userId(order.getUserId())
        .totalPrice(order.getTotalPrice()).createdAt(order.getCreatedAt())
        .orderItems(orderItemResponses).build();
  }

  private OrderItemResponse convertOrderItemToResponse(OrderItemEntity orderItem) {
    return OrderItemResponse.builder().orderItemId(orderItem.getOrderItemId())
        .productId(orderItem.getProductId()).quantity(orderItem.getQuantity())
        .price(orderItem.getPrice()).orderId(orderItem.getOrder().getOrderId()).build();
  }
}
