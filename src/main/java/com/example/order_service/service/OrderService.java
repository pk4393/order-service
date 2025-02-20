package com.example.order_service.service;

import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.exception.CreateOrderException;
import com.example.order_service.model.exception.InvalidOrderReportRequestException;
import com.example.order_service.model.exception.ProductNotFoundException;
import com.example.order_service.model.exception.UserNotFoundException;
import com.example.order_service.outbound.ProductApiClient;
import com.example.order_service.outbound.UserApiClient;
import com.example.order_service.outbound.model.Product;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.request.createorder.CreateOrderRequest;
import com.example.order_service.request.createorder.CreateOrderRequestItem;
import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.ReportResponse;
import com.example.order_service.response.user.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
  private final OrderRepository orderRepository;
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
    orderReportValidations(startDate, endDate, productId);

    if (productId != null && startDateTime != null && endDateTime != null) {
      orders =
          orderRepository.findOrdersByProductAndDateRange(productId, startDateTime, endDateTime);
    } else if (productId != null) {
      orders = orderRepository.findOrdersByProductId(productId);
    } else {
      orders = orderRepository.findOrdersByDateRange(startDateTime, endDateTime);
    }
    long totalProducts;
    if (productId != null) {
      totalProducts =
          orders.stream()
              .map(orderEntity -> orderEntity.getOrderItems().stream()
                  .map(OrderItemEntity::getProductId).filter(id -> id.equals(productId)).distinct()
                  .count())
              .reduce(Long::sum).orElse(0L);
    } else {
      totalProducts =
          orders.stream()
              .map(orderEntity -> orderEntity.getOrderItems().stream()
                  .map(OrderItemEntity::getProductId).distinct().count())
              .reduce(Long::sum).orElse(0L);
    }
    Double totalRevenue;
    if (productId != null) {
      totalRevenue = orders.stream()
          .map(orderEntity -> orderEntity.getOrderItems().stream()
              .filter(orderItemEntity -> orderItemEntity.getProductId().equals(productId))
              .map(orderItemEntity -> orderItemEntity.getPrice() * orderItemEntity.getQuantity())
              .reduce(Double::sum).orElse(0.0))
          .reduce(Double::sum).orElse(0.0);
    } else {
      totalRevenue =
          orders.stream().map(OrderEntity::getTotalPrice).reduce(Double::sum).orElse(0.0);
    }
    ReportResponse reportResponse = ReportResponse.builder().totalOrders(orders.size())
        .totalProducts(totalProducts).totalRevenue(totalRevenue).build();
    return BaseResponse.<ReportResponse>builder().status(HttpStatus.OK.name()).data(reportResponse)
        .build();
  }

  private void orderReportValidations(LocalDate startDate, LocalDate endDate, Long productId) {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new InvalidOrderReportRequestException("startDate cannot be after endDate.");
    }
    if (startDate != null && startDate.isAfter(LocalDate.now())) {
      throw new InvalidOrderReportRequestException("startDate cannot be in the future.");
    }
    if (endDate != null && endDate.isAfter(LocalDate.now())) {
      throw new InvalidOrderReportRequestException("endDate cannot be in the future.");
    }
    if (productId != null && productId <= 0) {
      throw new InvalidOrderReportRequestException("Invalid productId provided.");
    }
    if (productId != null) {
      try {
        productApiClient.findProduct(productId);
      } catch (HttpClientErrorException.NotFound e) {
        throw new ProductNotFoundException("No orders found for productId: " + productId);
      } catch (Exception e) {
        log.error("findProduct api call failed with: {}", e.getMessage());
        throw e;
      }
    }
  }

  public BaseResponse<List<Long>> getOrders(int page, int size, Long userId, Long productId) {
    Pageable pageable = PageRequest.of(page, size);
    Page<OrderEntity> orderPage;

    if (userId != null && productId != null) {
      ResponseEntity<BaseResponse<UserResponse>> userResponse = userApiClient.findUser(Math.toIntExact(userId));
      if (!userResponse.getStatusCode().is2xxSuccessful()) {
        log.error("User doesn't exist");
        throw new UserNotFoundException("User doesn't exist");
      }
      orderPage =
              orderRepository.findOrderIdsByUserIdAndProductIdPaginated(userId, productId, pageable);
    } else if (userId != null) {
      ResponseEntity<BaseResponse<UserResponse>> userResponse = userApiClient.findUser(Math.toIntExact(userId));
      if (!userResponse.getStatusCode().is2xxSuccessful()) {
        log.error("User doesn't exist");
        throw new UserNotFoundException("User doesn't exist");
      }
      orderPage = orderRepository.findOrdersByUserIdPaginated(userId, pageable);
    } else if (productId != null) {
      orderPage = orderRepository.findOrdersByProductIdPaginated(productId, pageable);
    } else {
      orderPage = orderRepository.findAll(pageable);
    }

    List<Long> orderIds = orderPage.getContent().stream().map(OrderEntity::getOrderId).toList();

    return BaseResponse.<List<Long>>builder().status(HttpStatus.OK.name()).data(orderIds).build();
  }

  @Transactional
  public BaseResponse<String> createOrder(Integer userId, CreateOrderRequest createOrderRequest) {

    ResponseEntity<BaseResponse<UserResponse>> userResponse = userApiClient.findUser(userId);

    if (!userResponse.getStatusCode().is2xxSuccessful()) {
      log.error("User doesn't exist");
      throw new CreateOrderException("User doesn't exist");
    }

    List<Long> productIds =
            createOrderRequest.getItems().stream().map(CreateOrderRequestItem::getProductId).toList();

    log.info("Product IDs: {}", productIds);

    BaseResponse<List<Product>> productResponse = productApiClient.findProductsByIds(productIds);

    log.info("Response: '{}'", productResponse);

    if (productResponse.getData() == null || productResponse.getData().size() != productIds.size()) {
      throw new CreateOrderException("One or more products don't exist");
    }

    OrderEntity orderEntity = new OrderEntity();

    if (userResponse.getBody() != null) {
      orderEntity.setUserId(userResponse.getBody().getData().getId());
    } else {
      throw new CreateOrderException("User response is null or invalid");
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
      price += createOrderRequest.getItems().get(i).getQuantity()
              * productResponse.getData().get(i).getPrice();
    }

    orderEntity.setTotalPrice(price);
    orderEntity.setOrderItems(orderItemEntities);

    orderRepository.save(orderEntity);

    // Return a BaseResponse containing the success message, now wrapped as a response
    return BaseResponse.<String>builder()
            .status(HttpStatus.OK.name()) // Success status
            .data("Order created successfully") // Success message
            .build();
  }

}
