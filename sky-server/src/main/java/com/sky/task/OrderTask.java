package com.sky.task;

import com.sky.entity.Order;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;

    @Scheduled(cron = "0 0/5 * * * ?") // execute once per minute
    public void processOrderInTimeout() {
        log.info("process order in timeout, {}", LocalDateTime.now());
        List<Order> orderList = orderMapper.selectByStatusAndOrderTime(Order.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));
        if (orderList == null || orderList.size() == 0) {
            return;
        }
        for (Order order : orderList) {
            order.setStatus(Order.CANCELLED);
            order.setCancelReason("order timeout");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // execute once per day
    public void processOrderInDispatch() {
        log.info("process order in dispatch, {}", LocalDateTime.now());
        List<Order> orderList = orderMapper.selectByStatusAndOrderTime(Order.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));
        if (orderList == null || orderList.size() == 0) {
            return;
        }
        for (Order order : orderList) {
            order.setStatus(Order.COMPLETED);
            orderMapper.update(order);
        }
    }
}
