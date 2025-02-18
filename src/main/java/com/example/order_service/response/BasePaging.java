package com.example.order_service.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasePaging implements Serializable {
  @Serial
  private static final long serialVersionUID = -502389155272339514L;
  private int totalPage;
  private long totalItems;
  private int page;
}