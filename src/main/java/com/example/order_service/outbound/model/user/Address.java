package com.example.order_service.outbound.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address  {
	private String country;
	private String address;
	private String city;
	private String postalCode;
	private Coordinates coordinates;
	private String stateCode;
	private String state;
}
