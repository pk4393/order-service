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
public class BaseResponse<T> implements Serializable {
  @Serial
  private static final long serialVersionUID = -8124697650202200102L;
  private T data;
  private String errorMessage;
  private Object errors;
  private String status;
  private BasePaging paging;
}