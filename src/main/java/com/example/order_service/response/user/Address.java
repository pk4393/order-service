package com.example.order_service.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address{
    private String country;
    private String address;
    private String city;
    private String postalCode;
    private Coordinates coordinates;
    private String stateCode;
    private String state;

    public String getCountry(){
        return country;
    }

    public String getAddress(){
        return address;
    }

    public String getCity(){
        return city;
    }

    public String getPostalCode(){
        return postalCode;
    }

    public Coordinates getCoordinates(){
        return coordinates;
    }

    public String getStateCode(){
        return stateCode;
    }

    public String getState(){
        return state;
    }
}
