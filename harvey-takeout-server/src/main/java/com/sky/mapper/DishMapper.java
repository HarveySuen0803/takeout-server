package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countCategoryId(Integer id);

    List<DishVO> selectByPage();

    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    @Select("select status from dish where id = #{id}")
    Integer selectStatusById(Long id);

    void deleteByIdList(List<Long> idList);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> selectByCondition(Dish dish);

    List<Dish> selectBySetmealId(Long setmealId);

    Integer countIdByCondition(Map dishMap);
}
