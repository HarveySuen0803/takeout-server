package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    public Dish selectById(Long id) {
        return dishMapper.selectById(id);
    }

    @Override
    public List<Dish> selectByCategoryId(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        return dishMapper.selectByCategoryId(dish);
    }

    @Override
    public DishVO selectWithFlavorListById(Long dishId) {
        // get Dish
        Dish dish = dishMapper.selectById(dishId);

        // get flavor list
        List<DishFlavor> flavorList = dishFlavorMapper.selectByDishId(dishId);

        // set Dish with flavor list
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavorList);

        return dishVO;
    }

    @Override
    public PageResult selectByPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> page = (Page<Dish>) dishMapper.selectByPage();
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional // Transactions are required when associating to multiple Table
    public void insert(DishDTO dishDTO) {
        // insert dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        // get flavorList
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        if (!(flavorList != null && flavorList.size() > 0)) {
            return;
        }

        // get primary key
        Long dishId = dish.getId();

        // set dishId
        for (DishFlavor flavor : flavorList) {
            flavor.setDishId(dishId);
        }

        // insert flavorList
        dishFlavorMapper.insertList(flavorList);
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // update Dish
        dishMapper.update(dish);

        // delete old DishFlavor
        dishFlavorMapper.deleteByDishId(dish.getId());

        // insert new DishFlavor
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        if (!(flavorList != null && flavorList.size() > 0)) {
            return;
        }
        for (DishFlavor flavor : flavorList) {
            flavor.setDishId(dishDTO.getId());
        }
        dishFlavorMapper.insertList(flavorList);
    }

    @Override
    @Transactional
    public void deleteByIdList(List<Long> dishIdList) {
        // can not delete Dish that on sale
        for (Long dishId : dishIdList) {
            if (dishMapper.selectStatusById(dishId).equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // can not delete Dish related by Setmeal
        List<Long> setmealIdList = setmealDishMapper.selectSetmealIdListByDishIdList(dishIdList);
        if (setmealIdList != null && setmealIdList.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // delete DishFlavor related by Dish
        dishFlavorMapper.deleteByDishIdList(dishIdList);

        // delete Dish
        dishMapper.deleteByIdList(dishIdList);
    }
}
