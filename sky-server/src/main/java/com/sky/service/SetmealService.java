package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult selectByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void insert(SetmealDTO setmealDTO);

    void deleteByIdList(List<Long> idList);

    SetmealVO selectById(Long setmealId);

    void update(SetmealDTO setmealDTO);

    void updateStatusById(Long id, Integer status);

    List<Setmeal> selectByCategoryId(Long categoryId);

    List<DishItemVO> selectSetmealDishById(Long id);
}
