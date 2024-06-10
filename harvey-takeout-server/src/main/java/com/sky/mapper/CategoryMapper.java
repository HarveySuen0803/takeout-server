package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    List<Category> selectByType(Integer type);

    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    void deleteById(Integer id);

    @AutoFill(OperationType.UPDATE)
    void update(Category category);
}
