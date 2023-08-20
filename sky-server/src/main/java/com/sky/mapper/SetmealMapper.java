package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countCategoryId(Integer id);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    List<Setmeal> selectByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteByIdList(List<Long> idList);

    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);

    @Select("select status from setmeal where id = #{id}")
    Integer selectStatusById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
