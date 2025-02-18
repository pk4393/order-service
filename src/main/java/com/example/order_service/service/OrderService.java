package com.example.order_service.service;

import org.springframework.stereotype.Service;

import com.example.order_service.outbound.ProductApiClient;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final ObjectMapper objectMapper;
  private final ProductApiClient productApiClient;
}
