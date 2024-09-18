package com.example.shoppingcartservice.models.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;

    // Getters and Setters
}
