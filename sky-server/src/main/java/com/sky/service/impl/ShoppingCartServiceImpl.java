package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public List<ShoppingCart> selectByUserId() {
        return shoppingCartMapper.selectByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void insert(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // if goods is in the shopping cart, then increase the number of goods
        // if goods is not exist in the shopping cart, then create the goods
        Long shoppingCartId = shoppingCartMapper.selectIdByCondition(shoppingCart);
        if (shoppingCartId != null) {
            shoppingCart = shoppingCartMapper.selectById(shoppingCartId);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart);
            return;
        }

        // if goods is dish, then set dish
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            Dish dish = dishMapper.selectById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setNumber(1);
        }

        // if goods is setmeal, then set setmeal
        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null) {
            Setmeal setmeal = setmealMapper.selectById(setmealId);
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setNumber(1);
        }

        // insert goods
        shoppingCartMapper.insert(shoppingCart);
    }
}
