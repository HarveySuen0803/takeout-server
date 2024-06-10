package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void insert(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> selectByUserId(Long userId);

    void deleteByUserId(Long userId);
}
