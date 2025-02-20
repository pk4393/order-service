package com.example.order_service.outbound.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private String firstName;
	private String lastName;
	private String password;
	private Address address;
	private String gender;
	private String phone;
	private int id;
	private String birthDate;
	private int age;
	private String email;
	private String username;
}
