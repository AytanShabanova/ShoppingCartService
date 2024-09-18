package com.example.shoppingcartservice.models.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<ShoppingCart> carts;

    // Getters and Setters
}
