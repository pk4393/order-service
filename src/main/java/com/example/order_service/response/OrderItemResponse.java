package com.example.order_service.response;

import java.io.Serial;
import java.io.Serializable;

import com.example.order_service.outbound.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
//@AllArgsConstructor
@Builder
@Data
public class OrderItemResponse implements Serializable {
  @Serial
  private static final long serialVersionUID = 1883951522016693533L;
  private Long productId;
  private Long orderItemId;
  private Long orderId;
  private Integer quantity;
  private Double price;
  private ProductsResponse product;


  public OrderItemResponse(Long orderItemId, Long orderId, Long productId, int quantity, double price, ProductsResponse product) {
    this.orderItemId = orderItemId;
    this.orderId = orderId;
    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
    this.product = product;
  }
}
