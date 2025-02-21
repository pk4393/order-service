package com.example.order_service.outbound;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.order_service.response.BaseResponse;
import com.example.order_service.response.user.UserResponse;

@HttpExchange("/api")
public interface UserApiClient {

    @GetExchange("/user/{userId}")
    ResponseEntity<BaseResponse<UserResponse>> findUser(@PathVariable Integer userId);

}
