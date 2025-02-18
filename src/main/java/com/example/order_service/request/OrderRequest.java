package com.example.order_service.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

  @NotNull(message = "User ID cannot be null")
  private Integer userId;

  @NotNull(message = "Product list cannot be null")
  private List<@NotNull OrderItemRequest> orderItems;
}
