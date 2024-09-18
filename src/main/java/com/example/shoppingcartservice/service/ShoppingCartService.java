package com.example.shoppingcartservice.service;

import com.example.shoppingcartservice.mapstruct.ShoppingCartMapper;
import com.example.shoppingcartservice.models.dto.ShoppingCartDTO;
import com.example.shoppingcartservice.models.entity.Product;
import com.example.shoppingcartservice.models.entity.ShoppingCart;
import com.example.shoppingcartservice.repo.ProductRepository;
import com.example.shoppingcartservice.repo.ShoppingCartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, ShoppingCartDTO> redisTemplate;
    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, 
                               ProductRepository productRepository,
                               RedisTemplate<String, ShoppingCartDTO> redisTemplate,
                               ShoppingCartMapper shoppingCartMapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    public ShoppingCartDTO createShoppingCart(String name) {
        ShoppingCart cart = new ShoppingCart();
        cart.setName(name);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    public ShoppingCartDTO addProductToCart(Long cartId, Long productId) {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                          .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId)
                          .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        cart.getProducts().add(product);
        shoppingCartRepository.save(cart);
        redisTemplate.delete("CART_" + cartId);  // Cache'i silmək
        return shoppingCartMapper.toDto(cart);
    }

    public ShoppingCartDTO removeProductFromCart(Long cartId, Long productId) {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                          .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        Product product = productRepository.findById(productId)
                          .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        cart.getProducts().remove(product);
        shoppingCartRepository.save(cart);
        redisTemplate.delete("CART_" + cartId);  // Cache'i silmək
        return shoppingCartMapper.toDto(cart);
    }

    public ShoppingCartDTO getShoppingCartById(Long id) {
        ShoppingCartDTO cachedCart = redisTemplate.opsForValue().get("CART_" + id);
        if (cachedCart != null) {
            return cachedCart;
        }

        ShoppingCart cart = shoppingCartRepository.findById(id)
                          .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        ShoppingCartDTO cartDTO = shoppingCartMapper.toDto(cart);
        redisTemplate.opsForValue().set("CART_" + id, cartDTO);
        return cartDTO;
    }
}
