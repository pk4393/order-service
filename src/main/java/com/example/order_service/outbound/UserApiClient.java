package com.example.order_service.outbound;

import com.example.order_service.outbound.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api")
public interface UserApiClient {

    @GetExchange("/user/{userId}")
    ResponseEntity<User> findUser(@PathVariable Integer userId);

}
