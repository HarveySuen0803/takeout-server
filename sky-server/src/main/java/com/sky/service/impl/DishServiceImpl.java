package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisConstant;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Dish selectById(Long id) {
        return dishMapper.selectById(id);
    }

    @Override
    public List<Dish> selectByCategoryId(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        return dishMapper.selectByCondition(dish);
    }

    @Override
    public DishVO selectWithFlavorListById(Long dishId) {
        // get Dish
        Dish dish = dishMapper.selectById(dishId);

        // get flavorList
        List<DishFlavor> flavorList = dishFlavorMapper.selectByDishId(dishId);

        // set Dish with flavorList
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavorList);

        return dishVO;
    }

    @Override
    public List<DishVO> selectWithFlavorListByCategoryId(Long categoryId) {
        // get Redis key
        String key = RedisConstant.DISH + categoryId;

        // query whether the data exist in Redis
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);

        // if the data exists in Redis, then return the data, otherwise query the data in MySQL
        if (dishVOList != null && dishVOList.size() > 0) {
            return dishVOList;
        }

        dishVOList = new ArrayList<>();

        // get dishList
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        List<Dish> dishList = dishMapper.selectByCondition(dish);

        // get dishVOList (dish with flavorList)
        for (Dish d : dishList) {
            DishVO dishVO = selectWithFlavorListById(d.getId());
            dishVOList.add(dishVO);
        }

        // create the data in Redis for the next query
        redisTemplate.opsForValue().set(key, dishVOList);

        return dishVOList;
    }

    @Override
    public PageResult selectByPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = (Page<DishVO>) dishMapper.selectByPage();
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void insert(DishDTO dishDTO) {
        // insert dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        // insert flavorList
        insertNewFlavorList(dishDTO);

        // clean cache
        cleanCache(RedisConstant.DISH + dish.getCategoryId());
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // update Dish
        dishMapper.update(dish);

        // delete old DishFlavor
        deleteOldFlavorListById(dish.getId());

        // insert new DishFlavor
        insertNewFlavorList(dishDTO);

        // delete Redis data
        cleanCache(RedisConstant.DISH_ALL);
    }

    public void deleteOldFlavorListById(Long id) {
        dishFlavorMapper.deleteByDishId(id);
    }

    public void insertNewFlavorList(DishDTO dishDTO) {
        // get flavorList
        List<DishFlavor> flavorList = dishDTO.getFlavors();

        if (!(flavorList != null && flavorList.size() > 0)) {
            return;
        }

        // set dishId
        for (DishFlavor flavor : flavorList) {
            flavor.setDishId(dishDTO.getId());
        }

        // insert flavorList
        dishFlavorMapper.insertList(flavorList);
    }

    @Override
    public void updateStatusById(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
        cleanCache(RedisConstant.DISH_ALL);
    }

    @Override
    @Transactional // Transactions are required when associating to multiple Table
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

        // clean cache
        cleanCache(RedisConstant.DISH_ALL);
    }

    public void cleanCache(String pattern) {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(pattern)));
    }
}
