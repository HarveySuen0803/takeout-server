package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {
    BusinessDataVO getBusinessData(LocalDateTime beginDateTime, LocalDateTime endDateTime);

    OrderOverViewVO getOrderOverView(LocalDateTime beginDateTime, LocalDateTime endDateTime);

    DishOverViewVO getDishOverView();

    SetmealOverViewVO getSetmealOverView();
}
