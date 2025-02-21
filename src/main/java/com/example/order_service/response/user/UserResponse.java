package com.example.order_service.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse{

    private int id;
    private String firstName;
    private String lastName;
    private String password;
    private Address address;
    private String gender;
    private String phone;
    private String birthDate;
    private int age;
    private String email;
    private String username;

}
