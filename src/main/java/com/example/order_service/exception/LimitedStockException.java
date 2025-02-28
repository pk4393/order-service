package com.example.order_service.exception;

import java.util.List;

public class LimitedStockException extends RuntimeException{

    public LimitedStockException(List<String> message) {
        super(String.valueOf(message));
    }
}
