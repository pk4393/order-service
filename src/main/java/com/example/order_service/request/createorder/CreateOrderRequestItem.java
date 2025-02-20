package com.example.order_service.request.createorder;

import lombok.Data;

@Data
public class CreateOrderRequestItem{
	private int quantity;
	private Long productId;
}
