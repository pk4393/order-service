package com.example.order_service.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates{
    private Object lng;
    private Object lat;

    public Object getLng(){
        return lng;
    }

    public Object getLat(){
        return lat;
    }
}
