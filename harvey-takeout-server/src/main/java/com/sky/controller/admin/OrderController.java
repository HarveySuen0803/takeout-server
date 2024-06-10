package com.sky.controller.admin;

import com.sky.dto.OrderCancelDTO;
import com.sky.dto.OrderConfirmDTO;
import com.sky.dto.OrderPageQueryDTO;
import com.sky.dto.OrderRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/conditionSearch")
    public Result<PageResult> selectPageByCondition(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("select Order page by condition, param is {}", orderPageQueryDTO);
        PageResult pageResult = orderService.selectPageByCondition(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        log.info("select Order statistics");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetails(@PathVariable Long id) {
        log.info("get order details, param is id={}", id);
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    public Result<String> confirmOrder(@RequestBody OrderConfirmDTO orderConfirmDTO) {
        log.info("confirm order, param is {}", orderConfirmDTO);
        orderService.confirmOrder(orderConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result<String> rejectOrder(@RequestBody OrderRejectionDTO orderRejectionDTO) throws Exception {
        log.info("reject order, param is {}", orderRejectionDTO);
        orderService.rejectOrder(orderRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result<String> cancelOrder(@RequestBody OrderCancelDTO orderCancelDTO) throws Exception {
        log.info("cancel order, param is {}", orderCancelDTO);
        orderService.cancelOrder(orderCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result<String> dispatchOrder(@PathVariable Long id) {
        log.info("dispatch order, param is orderId={}", id);
        orderService.dispatchOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result<String> completeOrder(@PathVariable Long id) {
        log.info("complete order, param is Order.id={}", id);
        orderService.completeOrder(id);
        return Result.success();
    }
}
