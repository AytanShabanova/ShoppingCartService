package com.example.shoppingcartservice;

import com.example.shoppingcartservice.mapstruct.ShoppingCartMapper;
import com.example.shoppingcartservice.models.dto.ShoppingCartDTO;
import com.example.shoppingcartservice.models.entity.Product;
import com.example.shoppingcartservice.models.entity.ShoppingCart;
import com.example.shoppingcartservice.repo.ProductRepository;
import com.example.shoppingcartservice.repo.ShoppingCartRepository;
import com.example.shoppingcartservice.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisTemplate<String, ShoppingCartDTO> redisTemplate;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @Mock
    private ValueOperations<String, ShoppingCartDTO> valueOperations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testCreateShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setName("Test Cart");

        ShoppingCartDTO cartDTO = new ShoppingCartDTO();
        cartDTO.setId(1L);
        cartDTO.setName("Test Cart");

        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(cartDTO);

        ShoppingCartDTO result = shoppingCartService.createShoppingCart("Test Cart");

        assertNotNull(result);
        assertEquals("Test Cart", result.getName());
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    public void testAddProductToCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        
        Product product = new Product();
        product.setId(1L);

        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ShoppingCartDTO cartDTO = new ShoppingCartDTO();
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(cartDTO);

        ShoppingCartDTO result = shoppingCartService.addProductToCart(1L, 1L);

        assertNotNull(result);
        verify(shoppingCartRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(redisTemplate, times(1)).delete("CART_1");
    }

    @Test
    public void testRemoveProductFromCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        
        Product product = new Product();
        product.setId(1L);

        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ShoppingCartDTO cartDTO = new ShoppingCartDTO();
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(cartDTO);

        ShoppingCartDTO result = shoppingCartService.removeProductFromCart(1L, 1L);

        assertNotNull(result);
        verify(shoppingCartRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(redisTemplate, times(1)).delete("CART_1");
    }

    @Test
    public void testGetShoppingCartById_WithCache() {
        ShoppingCartDTO cachedCart = new ShoppingCartDTO();
        cachedCart.setId(1L);
        cachedCart.setName("Cached Cart");

        when(redisTemplate.opsForValue().get("CART_1")).thenReturn(cachedCart);

        ShoppingCartDTO result = shoppingCartService.getShoppingCartById(1L);

        assertNotNull(result);
        assertEquals("Cached Cart", result.getName());
        verify(redisTemplate, times(1)).opsForValue().get("CART_1");
        verify(shoppingCartRepository, times(0)).findById(anyLong());
    }

    @Test
    public void testGetShoppingCartById_WithoutCache() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        
        ShoppingCartDTO cartDTO = new ShoppingCartDTO();
        cartDTO.setId(1L);
        cartDTO.setName("Cart from DB");

        when(redisTemplate.opsForValue().get("CART_1")).thenReturn(null);
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(cartDTO);

        ShoppingCartDTO result = shoppingCartService.getShoppingCartById(1L);

        assertNotNull(result);
        assertEquals("Cart from DB", result.getName());
        verify(redisTemplate, times(1)).opsForValue().get("CART_1");
        verify(shoppingCartRepository, times(1)).findById(1L);
        verify(redisTemplate, times(1)).opsForValue().set("CART_1", cartDTO);
    }

    @Test
    public void testGetShoppingCartById_NotFound() {
        when(redisTemplate.opsForValue().get("CART_1")).thenReturn(null);
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.getShoppingCartById(1L);
        });

        assertEquals("Cart not found", exception.getMessage());
        verify(shoppingCartRepository, times(1)).findById(1L);
    }
}
