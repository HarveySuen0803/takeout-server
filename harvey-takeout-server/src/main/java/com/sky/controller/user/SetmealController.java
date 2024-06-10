package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    public Result<List<Setmeal>> selectByCategoryId(Long categoryId) {
        log.info("select Setmeal by categoryId, param is categoryId={}", categoryId);
        List<Setmeal> setmealList = setmealService.selectByCategoryId(categoryId);
        return Result.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> selectSetmealDishById(@PathVariable("id") Long id) {
        log.info("select SetmealDish by Id, param is id={}", id);
        List<DishItemVO> setmealDishList = setmealService.selectSetmealDishById(id);
        return Result.success(setmealDishList);
    }
}
