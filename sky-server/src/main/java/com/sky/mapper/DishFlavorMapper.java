package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertList(List<DishFlavor> flavorList);

    List<DishFlavor> selectByDishId(Long dishId);

    void deleteByDishId(Long dishId);

    void deleteByDishIdList(List<Long> dishIdList);
}
