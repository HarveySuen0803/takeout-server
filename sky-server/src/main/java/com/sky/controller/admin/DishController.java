package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    DishService dishService;

    @GetMapping("/{id}")
    public Result<DishVO> selectById(@PathVariable Long id) {
        log.info("select Dish with flavor field by id, param is (id={})", id);
        DishVO dishVO = dishService.selectWithFlavorListById(id);
        return Result.success(dishVO);
    }

    @GetMapping("/list")
    public Result<List<Dish>> selectByCategoryId(Long categoryId) {
        log.info("select Category by id, param is (categoryId={})", categoryId);
        List<Dish> dishList = dishService.selectByCategoryId(categoryId);
        return Result.success(dishList);
    }

    @GetMapping("/page")
    public Result<PageResult> selectByPage(DishPageQueryDTO dishPageQueryDTO) {
        log.info("select Dish by page, param is {}", dishPageQueryDTO);
        PageResult pageResult = dishService.selectByPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result<String> insert(DishDTO dishDTO) {
        log.info("insert Dish, param is {}", dishDTO);
        dishService.insert(dishDTO);
        return Result.success();
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("update Dish, param is {}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result<String> deleteList(@RequestParam(value = "ids") List<Long> idList) {
        log.info("delete Dish list, param is (idList={})", idList);
        dishService.deleteByIdList(idList);
        return Result.success();
    }
}
