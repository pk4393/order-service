package com.example.order_service.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1883951522016693533L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderItemId;

  @Column(nullable = false)
  private Long orderId;

  @Column(nullable = false)
  private Long productId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Double price;
}

