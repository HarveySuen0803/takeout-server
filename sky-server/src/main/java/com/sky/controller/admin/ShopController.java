package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    ShopService shopService;

    @PutMapping("/{status}")
    public Result<String> updateStatus(@PathVariable Integer status) {
        log.info("update Shop status, param is (status={})", status);
        shopService.updateStatus(status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> selectStatus() {
        log.info("select Shop status");
        Integer status = shopService.selectStatus();
        return Result.success(status);
    }
}
