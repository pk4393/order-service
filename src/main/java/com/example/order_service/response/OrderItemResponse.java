package com.example.order_service.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse implements Serializable {
  @Serial
  private static final long serialVersionUID = 1883951522016693533L;
  private Long productId;
  private Integer quantity;
  private Double price;
}
