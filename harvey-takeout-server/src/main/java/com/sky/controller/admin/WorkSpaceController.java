package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    public Result<BusinessDataVO> getBusinessData() {
        log.info("get today's business data");
        LocalDateTime beginDateTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginDateTime, endDateTime);
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> getOrderOverView(){
        log.info("get today's order overview");
        LocalDateTime beginDateTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.now().with(LocalTime.MAX);
        OrderOverViewVO orderOverViewVO = workspaceService.getOrderOverView(beginDateTime, endDateTime);
        return Result.success(orderOverViewVO);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> dishOverView(){
        log.info("get today's dish overview");
        DishOverViewVO dishOverViewVO = workspaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> setmealOverView(){
        log.info("get today's setmeal overview");
        SetmealOverViewVO setmealOverViewVO = workspaceService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }
}
