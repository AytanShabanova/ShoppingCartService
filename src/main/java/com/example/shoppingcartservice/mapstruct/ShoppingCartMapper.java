package com.example.shoppingcartservice.mapstruct;

import com.example.shoppingcartservice.models.dto.ShoppingCartDTO;
import com.example.shoppingcartservice.models.entity.ShoppingCart;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartDTO toDto(ShoppingCart shoppingCart);
    ShoppingCart toEntity(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCartDTO> toDtoList(List<ShoppingCart> shoppingCarts);
}
