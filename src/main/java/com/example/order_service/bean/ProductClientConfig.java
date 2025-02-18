package com.example.order_service.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.order_service.outbound.ProductApiClient;

@Configuration
public class ProductClientConfig {

  @Bean
  public ProductApiClient getProductApiClient() {
    RestClient restClient = RestClient.builder().baseUrl("http://10.2.4.18:8084").build();
    HttpServiceProxyFactory proxyFactory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return proxyFactory.createClient(ProductApiClient.class);
  }
}
