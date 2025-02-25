package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
import com.example.order_service.response.OrderItemResponse;
import com.example.order_service.response.OrderResponse;
import com.example.order_service.response.OrdersListingResponse;
import com.example.order_service.response.ProductsResponse;
import com.example.order_service.response.ReportResponse;
import com.example.order_service.response.user.UserResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    List<Long> productIds;
    if (productId != null) {
      productIds = orders.stream()
          .flatMap(orderEntity -> orderEntity.getOrderItems().stream()
              .map(OrderItemEntity::getProductId).filter(id -> id.equals(productId)).distinct())
          .toList();

    } else {
      productIds = orders.stream().flatMap(orderEntity -> orderEntity.getOrderItems().stream()
          .map(OrderItemEntity::getProductId).distinct()).toList();
    }
    totalProducts = productIds.size();
    List<String> products = fetchProducts(productIds.stream().distinct().toList());
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
        .totalProducts(totalProducts).totalRevenue(totalRevenue).products(products).build();
    return BaseResponse.<ReportResponse>builder().status(HttpStatus.OK.name()).data(reportResponse)
        .build();
  }

  private List<String> fetchProducts(List<Long> productIds) {
    try {
      BaseResponse<List<Product>> products = productApiClient.findProductsByIds(productIds);
      if ("OK".equals(products.getStatus())) {
        return products.getData().stream().map(Product::getBrand).toList();
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return Collections.emptyList();
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
      } catch (HttpClientErrorException.NotFound ex) {
        throw new ProductNotFoundException("No orders found for productId: " + productId);
      } catch (Exception ex) {
        log.error("findProduct api call failed with: {}", ex.getMessage());
        throw ex;
      }
    }
  }

    public BaseResponse<OrdersListingResponse> getOrders(int page, int size, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Pageable pageable = PageRequest.of(page, size);

        // Fetch user details
        ResponseEntity<BaseResponse<UserResponse>> userResponseEntity = userApiClient.findUser(Math.toIntExact(userId));

        if (!userResponseEntity.getStatusCode().is2xxSuccessful() || userResponseEntity.getBody() == null) {
            throw new UserNotFoundException("User not found");
        }

        UserResponse user = userResponseEntity.getBody().getData();

        // Fetch orders
        Page<OrderEntity> ordersPage = orderRepository.findOrdersByUserIdPaginated(userId, pageable);
        List<OrderEntity> orders = ordersPage.getContent();

        if (orders.isEmpty()) {
            return BaseResponse.<OrdersListingResponse>builder()
                    .status(HttpStatus.OK.name())
                    .data(new OrdersListingResponse(user, List.of()))
                    .build();
        }

        // Fetch order items
        List<Long> orderIds = orders.stream().map(OrderEntity::getOrderId).toList();
        System.out.println("Orders from DB: " + orderIds);

        List<OrderItemEntity> orderItems = orderRepository.findOrderItemsByOrderIds(orderIds);

        List<Long> productIds = orderItems.stream().map(OrderItemEntity::getProductId).distinct().toList();
        BaseResponse<List<Product>> productResponse = productApiClient.findProductsByIds(productIds);

        // Map products
        Map<Long, ProductsResponse> productMap = Optional.ofNullable(productResponse.getData())
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.toMap(Product::getId, ProductsResponse::new));

        // Map order items to response
        Map<Long, List<OrderItemResponse>> orderItemMap = orderItems.stream()
                .filter(item -> item.getOrderItemId() != null && item.getOrder() != null) // Ensure order ID is not null
                .map(item -> {
                    Long correctOrderId = item.getOrder().getOrderId(); // Explicitly extract correct Order ID
                    ProductsResponse product = productMap.get(item.getProductId());

                    return new OrderItemResponse(
                            item.getOrderItemId(),
                            correctOrderId,
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice(),
                            product
                    );
                })
                .collect(Collectors.groupingBy(o -> o.getOrderId(), Collectors.toList()));


        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> {
                    List<OrderItemResponse> items = orderItemMap.getOrDefault(order.getOrderId(), List.of());
                    return new OrderResponse(
                            order.getOrderId(),
                            user.getId(),
                            items,
                            order.getTotalPrice()
                    );
                })
                .toList();

        // Build response
        OrdersListingResponse response = new OrdersListingResponse(user, orderResponses);
        return BaseResponse.<OrdersListingResponse>builder()
                .status(HttpStatus.OK.name())
                .data(response)
                .build();
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
