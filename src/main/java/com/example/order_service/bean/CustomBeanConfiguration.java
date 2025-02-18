package com.example.order_service.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class CustomBeanConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
