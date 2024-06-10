package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.Order;
import com.sky.entity.ShoppingCart;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    PageResult selectPage(int pageNum, int pageSize, Integer status);

    OrderVO selectWithOrderDetailById(Long id);

    void userCancelOrderById(Long id) throws Exception;

    void repetition(Long id);

    PageResult selectPageByCondition(OrderPageQueryDTO orderPageQueryDTO);

    OrderStatisticsVO statistics();

    OrderVO getOrderDetails(Long id);

    void confirmOrder(OrderConfirmDTO orderConfirmDTO);

    void rejectOrder(OrderRejectionDTO orderRejectionDTO) throws Exception;

    void cancelOrder(OrderCancelDTO orderCancelDTO) throws Exception;

    void dispatchOrder(Long id);

    void completeOrder(Long id);

    void remindOrder(Long id);
    
    void insertOrderDetailList(Long orderId, List<ShoppingCart> shoppingCartList);
    
    Order encapOrder(OrderSubmitDTO orderSubmitDTO, Long userId, AddressBook addressBook);
    
    OrderSubmitVO encapOrderSubmitVO(Order order);
    
    void insert(Order order);
}
