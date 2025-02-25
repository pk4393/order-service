package com.example.order_service.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse implements Serializable {
  @Serial
  private static final long serialVersionUID = -1592691973019497814L;
  private long totalOrders;
  private long totalProducts;
  private Double totalRevenue;
  private List<String> products;
}
