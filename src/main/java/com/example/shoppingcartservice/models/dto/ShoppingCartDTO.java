package com.example.shoppingcartservice.models.dto;

import lombok.Data;

import java.util.List;
@Data
public class ShoppingCartDTO {
    private Long id;
    private String name;
    private List<ProductDTO> products;

    // Getters and Setters
}
