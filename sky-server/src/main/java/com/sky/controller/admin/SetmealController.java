package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @PostMapping("/status/{status}")
    public Result<String> updateStatusById(Long id, @PathVariable Integer status) {
        log.info("update Setmeal status Field by id, param is (id={}, status={})", id, status);
        setmealService.updateStatusById(id, status);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> selectById(@PathVariable Long id) {
        log.info("select Setmeal by id, param is (id={})", id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("update Setmeal, param is {}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result<String> deleteByIdList(@RequestParam("ids") List<Long> idList) {
        log.info("delete Setmeal list, param is {}", idList);
        setmealService.deleteByIdList(idList);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> selectByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("select Setmeal by page, param is {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.selectByPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result<String> insert(@RequestBody SetmealDTO setmealDTO) {
        log.info("save Setmeal, param is {}", setmealDTO);
        setmealService.insert(setmealDTO);
        return Result.success();
    }
}
