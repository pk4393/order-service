package com.example.order_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
  private Long orderId;
  private Integer userId;
  private List<OrderItemDTO> orderItems;
  private Double totalPrice;
  private LocalDateTime createdAt;
}
