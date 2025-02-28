package com.example.order_service.request.createorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest{

	@NotEmpty(message = "Order items cannot be empty")
	private List<@Valid CreateOrderRequestItem> items;

}