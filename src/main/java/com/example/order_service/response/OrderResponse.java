package com.example.order_service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse implements Serializable {
  @Serial
  private static final long serialVersionUID = -285636647845937370L;
  private Long orderId;
  private Integer userId;
  private List<OrderItemResponse> orderItems;
  private Double totalPrice;
  private LocalDateTime createdAt;
}
