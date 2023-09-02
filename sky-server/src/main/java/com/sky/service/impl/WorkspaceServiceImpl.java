package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Order;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.SetmealService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.apache.catalina.UserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public BusinessDataVO getBusinessData(LocalDateTime beginDateTime, LocalDateTime endDateTime) {
        // count order
        Map orderMap = new HashMap();
        orderMap.put("beginDateTime", beginDateTime);
        orderMap.put("endDateTime", endDateTime);
        Integer orderCount = orderMapper.countIdByCondition(orderMap);

        // count new user
        Map userMap = new HashMap();
        userMap.put("beginDateTime", beginDateTime);
        userMap.put("endDateTime", endDateTime);
        Integer newUserCount = userMapper.countIdByCondition(userMap);

        // count turnover
        orderMap.put("status", Order.COMPLETED);
        Double turnover = orderMapper.sumAmountByCondition(orderMap);
        if (turnover == null) {
            turnover = 0.0;
        }

        // count valid order
        Integer validOrderCount = orderMapper.countIdByCondition(orderMap);

        // set order completion rate
        Double orderCompletionRate = 0.0;
        if (orderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / orderCount;
        }

        // set unit price
        Double unitPrice = 0.0;
        if (validOrderCount != 0) {
            unitPrice = turnover / validOrderCount;
        }

        BusinessDataVO businessDataVO = new BusinessDataVO();
        businessDataVO.setTurnover(turnover);
        businessDataVO.setValidOrderCount(validOrderCount);
        businessDataVO.setOrderCompletionRate(orderCompletionRate);
        businessDataVO.setUnitPrice(unitPrice);
        businessDataVO.setNewUsers(newUserCount);

        return businessDataVO;
    }

    @Override
    public OrderOverViewVO getOrderOverView(LocalDateTime beginDateTime, LocalDateTime endDateTime) {
        Map orderMap = new HashMap();
        orderMap.put("beginDateTime", beginDateTime);
        orderMap.put("endDateTime", endDateTime);

        Integer countOrder = orderMapper.countIdByCondition(orderMap);

        orderMap.put("status", Order.TO_BE_CONFIRMED);
        Integer countOrderToBeConfirmed = orderMapper.countIdByCondition(orderMap);

        orderMap.put("status", Order.CONFIRMED);
        Integer countOrderConfirmed = orderMapper.countIdByCondition(orderMap);

        orderMap.put("status", Order.COMPLETED);
        Integer countOrderCompleted = orderMapper.countIdByCondition(orderMap);

        orderMap.put("status", Order.CANCELLED);
        Integer countOrderCanceled = orderMapper.countIdByCondition(orderMap);

        OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
        orderOverViewVO.setAllOrders(countOrder);
        orderOverViewVO.setWaitingOrders(countOrderToBeConfirmed);
        orderOverViewVO.setDeliveredOrders(countOrderConfirmed);
        orderOverViewVO.setCancelledOrders(countOrderCanceled);
        orderOverViewVO.setCompletedOrders(countOrderCompleted);

        return orderOverViewVO;
    }
    
    @Override
    public DishOverViewVO getDishOverView() {
        Map dishMap = new HashMap();

        dishMap.put("status", StatusConstant.ENABLE);
        Integer countDishEnabled = dishMapper.countIdByCondition(dishMap);

        dishMap.put("status", StatusConstant.DISABLE);
        Integer countDishDisabled = dishMapper.countIdByCondition(dishMap);

        DishOverViewVO dishOverViewVO = new DishOverViewVO();
        dishOverViewVO.setSold(countDishEnabled);
        dishOverViewVO.setDiscontinued(countDishDisabled);

        return dishOverViewVO;
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Map setmealMap = new HashMap();

        setmealMap.put("status", StatusConstant.ENABLE);
        Integer countDishEnabled = setmealMapper.countIdByCondition(setmealMap);

        setmealMap.put("status", StatusConstant.DISABLE);
        Integer countDishDisabled = setmealMapper.countIdByCondition(setmealMap);

        SetmealOverViewVO setmealOverViewVO = new SetmealOverViewVO();
        setmealOverViewVO.setSold(countDishEnabled);
        setmealOverViewVO.setDiscontinued(countDishDisabled);

        return setmealOverViewVO;
    }

    public Map getTodayTimeMap() {
        LocalDateTime beginDateTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.now().with(LocalTime.MAX);

        Map map = new HashMap();
        map.put("beginDateTime", beginDateTime);
        map.put("endDateTime", endDateTime);

        return map;
    }
}
