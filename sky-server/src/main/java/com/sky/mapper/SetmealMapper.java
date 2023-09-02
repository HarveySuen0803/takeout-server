package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countCategoryId(Integer id);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    List<SetmealVO> selectByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteByIdList(List<Long> idList);

    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);

    @Select("select status from setmeal where id = #{id}")
    Integer selectStatusById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    List<Setmeal> selectByCondition(Setmeal setmeal);

    Integer countIdByCondition(Map setmealMap);
}
