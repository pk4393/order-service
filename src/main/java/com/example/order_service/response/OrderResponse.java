package com.example.order_service.response;

import com.example.order_service.outbound.model.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Data
public class OrderResponse implements Serializable {
  @Serial
  private static final long serialVersionUID = -285636647845937370L;
  private List<OrderItemResponse> orderItems;
  private Double totalPrice;
  private List<Product> productsResponse;



  public OrderResponse(List<OrderItemResponse> orderItemResponseList, Double totalPrice) {
    this.totalPrice = totalPrice;
    this.orderItems=orderItemResponseList;
  }
}
