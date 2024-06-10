package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/page")
    public Result<PageResult> selectByPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("select Category by page, param is {}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.selectByPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    public Result<List<Category>> selectByType(Integer type) {
        log.info("select Category by type, param is {}", type);
        List<Category> categoryList = categoryService.selectByType(type);
        return Result.success(categoryList);
    }

    @PostMapping
    public Result<String> insert(@RequestBody CategoryDTO categoryDTO) {
        log.info("insert Category, param is {}", categoryDTO);
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    @PutMapping
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("update Category, param is {}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(Long id, @PathVariable Integer status) {
        log.info("update Category status, param is (id={}, status={})", id, status);
        categoryService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping
    public Result<String> deleteById(Integer id) {
        log.info("delete Category by id, param is (id={})", id);
        categoryService.deleteById(id);
        return Result.success();
    }
}
