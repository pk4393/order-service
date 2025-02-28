package com.example.order_service.outbound;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import com.example.order_service.outbound.model.Product;
import com.example.order_service.request.UpdateProductRequest;
import com.example.order_service.response.BaseResponse;

@HttpExchange("/api/products")
public interface ProductApiClient {

  @GetExchange("/{productId}")
  BaseResponse<Product> findProduct(@PathVariable long productId);

  @PostExchange("/find-by-ids")
  BaseResponse<List<Product>> findProductsByIds(@RequestBody List<Long> productIds);

  @PutExchange("/{id}")
  BaseResponse<Product> updateProduct(@PathVariable long id,
      @RequestBody UpdateProductRequest request);
}
