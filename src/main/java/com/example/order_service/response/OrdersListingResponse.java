package com.example.order_service.response;

import com.example.order_service.response.user.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class OrdersListingResponse implements Serializable {
    private UserResponse user;
    private List<OrderResponse> orders;

    public OrdersListingResponse(UserResponse user) {
        this.user = user;
    }

    public OrdersListingResponse(UserResponse user, List<OrderResponse> orders) {
        this.user = user;
        this.orders = orders;
    }
}