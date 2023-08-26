package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<String> insert(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("save to ShoppingCart, param is {}", shoppingCartDTO);
        shoppingCartService.insert(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> selectByUserId() {
        log.info("select ShoppingCart by userId");
        List<ShoppingCart> shoppingCartList = shoppingCartService.selectByUserId();
        return Result.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public Result<String> deleteByUserId() {
        log.info("delete ShoppingCart by userId");
        shoppingCartService.deleteByUserId();
        return Result.success();
    }
}
