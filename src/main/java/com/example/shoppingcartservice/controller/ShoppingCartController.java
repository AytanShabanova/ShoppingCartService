package com.example.shoppingcartservice.controller;

import com.example.shoppingcartservice.models.dto.ShoppingCartDTO;
import com.example.shoppingcartservice.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopping-carts")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping
    public ResponseEntity<ShoppingCartDTO> createShoppingCart(@RequestParam String name) {
        return ResponseEntity.ok(shoppingCartService.createShoppingCart(name));
    }

    @PostMapping("/{cartId}/product")
    public ResponseEntity<ShoppingCartDTO> addProductToCart(@PathVariable Long cartId, @RequestParam Long productId) {
        return ResponseEntity.ok(shoppingCartService.addProductToCart(cartId, productId));
    }

    @DeleteMapping("/{cartId}/product/{productId}")
    public ResponseEntity<ShoppingCartDTO> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        return ResponseEntity.ok(shoppingCartService.removeProductFromCart(cartId, productId));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<ShoppingCartDTO> getShoppingCartById(@PathVariable Long cartId) {
        return ResponseEntity.ok(shoppingCartService.getShoppingCartById(cartId));
    }
}
