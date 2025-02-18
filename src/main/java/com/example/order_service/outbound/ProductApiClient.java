package com.example.order_service.outbound;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.example.order_service.outbound.model.Product;
import com.example.order_service.response.BaseResponse;

@HttpExchange("/api/products")
public interface ProductApiClient {

  @GetExchange("/{userId}")
  BaseResponse<Product> findProduct(@PathVariable long productId);

  @PostExchange
  BaseResponse<List<Product>> findProductsByIds(@RequestBody List<Long> productIds);
}
