package com.example.order_service.bean;

import com.example.order_service.outbound.UserApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class UserClientConfiguration {

    @Bean
    public UserApiClient getUserApiClient() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:8081").build();
        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return proxyFactory.createClient(UserApiClient.class);
    }

}
