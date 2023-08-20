package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void insert(DishDTO dishDTO);

    Dish selectById(Long id);

    PageResult selectByPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteByIdList(List<Long> dishIdList);

    DishVO selectWithFlavorListById(Long dishId);

    void update(DishDTO dishDTO);

    List<Dish> selectByCategoryId(Long categoryId);
}
