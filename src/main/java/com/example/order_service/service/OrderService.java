package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.order_service.outbound.UserApiClient;
import com.example.order_service.outbound.model.Product;
import com.example.order_service.outbound.model.user.User;
import com.example.order_service.request.createorder.CreateOrderRequest;
import com.example.order_service.request.createorder.CreateOrderRequestItem;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Slf4j
public class OrderService {
  private final OrderRepository orderRepository;
  private final ObjectMapper objectMapper;
  private final ProductApiClient productApiClient;
  private final UserApiClient userApiClient;

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

  @Transactional
  public String createOrder(Integer userId, CreateOrderRequest createOrderRequest) {

    ResponseEntity<User> userResponse = userApiClient.findUser(userId);
    if (!userResponse.getStatusCode().is2xxSuccessful()) {
      log.error("User doesn't exist");
      throw new RuntimeException("User doesn't exist");
    }

    List<Long> productIds = createOrderRequest.getItems().stream()
            .map(CreateOrderRequestItem::getProductId)
            .toList();

    log.info("Product IDs: {}", productIds);

    BaseResponse<List<Product>> productResponse = productApiClient.findProductsByIds(productIds);

    log.info("Responnsee: '{}'", productResponse);

    if (productResponse == null || productResponse.getData() == null) {
      throw new RuntimeException("Product API response is null or invalid");
    }

    OrderEntity orderEntity = new OrderEntity();

    if (userResponse.getBody() != null) {
      orderEntity.setUserId(userResponse.getBody().getId());
    } else {
      throw new RuntimeException("User response is null or invalid");
    }

    List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    double price = 0.0;

    for (int i = 0; i < productResponse.getData().size(); i++) {
      OrderItemEntity orderItem = new OrderItemEntity();
      orderItem.setProductId(productResponse.getData().get(i).getId());
      orderItem.setQuantity(createOrderRequest.getItems().get(i).getQuantity());
      orderItem.setPrice(productResponse.getData().get(i).getPrice());

      orderItem.setOrder(orderEntity);

      orderItemEntities.add(orderItem);

      price += createOrderRequest.getItems().get(i).getQuantity() * productResponse.getData().get(i).getPrice();
    }

    orderEntity.setTotalPrice(price);

    orderEntity.setOrderItems(orderItemEntities);

    orderRepository.save(orderEntity);

    return "Order created successfully";
  }

}
