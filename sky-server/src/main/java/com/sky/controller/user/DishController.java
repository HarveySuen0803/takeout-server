package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("userDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    public Result<List<DishVO>> selectWithFlavorListByCategoryId(Long categoryId) {
        log.info("select Dish with flavorList by categoryId, param is categoryId={}", categoryId);
        List<DishVO> list = dishService.selectWithFlavorListByCategoryId(categoryId);
        return Result.success(list);
    }
}
