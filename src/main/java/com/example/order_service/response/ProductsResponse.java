package com.example.order_service.response;

import java.io.Serializable;

import com.example.order_service.outbound.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductsResponse implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Double price;
    private String category;
    private String brand;

    public ProductsResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.category = product.getCategory();
    }
}
