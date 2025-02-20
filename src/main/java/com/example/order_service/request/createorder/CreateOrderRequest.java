package com.example.order_service.request.createorder;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest{
	private List<CreateOrderRequestItem> items;
}