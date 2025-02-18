package com.example.order_service.outbound.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product implements Serializable {
  @Serial
  private static final long serialVersionUID = -8523348558187575484L;
  private List<String> images;
  private Double rating;
  private String description;
  private Integer weight;
  private String title;
  private List<String> tags;
  private Double discountPercentage;
  private Double price;
  private Long id;
  private String category;
  private Integer stock;
  private String sku;
  private String brand;
}
