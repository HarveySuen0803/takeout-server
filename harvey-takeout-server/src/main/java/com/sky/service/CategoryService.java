package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    PageResult selectByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    List<Category> selectByType(Integer type);

    void insert(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void updateStatus(Long id, Integer status);

    void deleteById(Integer id);
}
