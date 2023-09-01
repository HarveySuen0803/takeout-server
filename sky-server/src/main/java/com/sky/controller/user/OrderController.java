package com.sky.controller.user;

import com.sky.dto.OrderSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        log.info("submit order, param is {}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> selectPage(int page, int pageSize, Integer status) {
        log.info("select Order page, param is page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult pageResult = orderService.selectPage(page, pageSize, status);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> selectById(@PathVariable Long id) {
        log.info("select Order by id, param is id={}", id);
        OrderVO orderVO = orderService.selectWithOrderDetailById(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    public Result<String> cancelOrderById(@PathVariable Long id) throws Exception {
        log.info("cancel Order, param is id={}", id);
        orderService.userCancelOrderById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    public Result<String> repetition(@PathVariable Long id) {
        log.info("repetition order, param is id={}", id);
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    public Result<String> remindOrder(@PathVariable Long id) {
        log.info("remind order, ");
        orderService.remindOrder(id);
        return Result.success();
    }
}
