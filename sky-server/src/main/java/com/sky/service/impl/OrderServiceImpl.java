package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.Order;
import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    WeChatPayUtil weChatPayUtil;

    @Transactional
    @Override
    public OrderSubmitVO submit(OrderSubmitDTO orderSubmitDTO) {
        Long userId = BaseContext.getCurrentId();

        // if AddressBook is empty, then throw Exception
        AddressBook addressBook = addressBookMapper.selectById(orderSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // if ShoppingCart is empty, then throw Exception
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectByUserId(userId);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // insert Order
        Order order = new Order();
        BeanUtils.copyProperties(orderSubmitDTO, order);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Order.UN_PAID);
        order.setStatus(Order.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setAddress(addressBook.getDetail());
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        order.setUserId(userId);
        orderMapper.insert(order);

        // insert OrderDetail
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertList(orderDetailList);

        // delete ShoppingCart
        shoppingCartMapper.deleteByUserId(userId);

        // encap OrderSubmitVO
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(order.getId());
        orderSubmitVO.setOrderTime(order.getOrderTime());
        orderSubmitVO.setOrderNumber(order.getNumber());
        orderSubmitVO.setOrderAmount(order.getAmount());

        return orderSubmitVO;
    }

    @Override
    public PageResult selectPage(int pageNum, int pageSize, Integer status) {
        // select Order page
        PageHelper.startPage(pageNum, pageSize);
        OrderPageQueryDTO orderPageQueryDTO = new OrderPageQueryDTO();
        orderPageQueryDTO.setUserId(BaseContext.getCurrentId());
        orderPageQueryDTO.setStatus(status);
        Page<Order> page = (Page<Order>) orderMapper.selectPage(orderPageQueryDTO);

        if (page == null || page.getTotal() == 0) {
            return null;
        }

        // set orderVOList
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : page) {
            List<OrderDetail> orderDetailList = orderDetailMapper.selectListByOrderId(order.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }

        return new PageResult(orderVOList.size(), orderVOList);
    }

    @Override
    public OrderVO selectWithOrderDetailById(Long id) {
        Order order = orderMapper.selectById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectListByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void userCancelOrderById(Long id) throws Exception {
        Order order = orderMapper.selectById(id);

        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // if order status is 2, then refund
        // if (order.getStatus().equals(Order.TO_BE_CONFIRMED)) {
        //     weChatPayUtil.refund(order.getNumber(), order.getNumber(), new BigDecimal("0.01"), new BigDecimal("0.01"));
        //     order.setPayStatus(Order.REFUND);
        // }

        order.setStatus(Order.CANCELLED);
        order.setCancelReason("user cancel");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    public void repetition(Long id) {
        // get orderDetailList
        List<OrderDetail> orderDetailList = orderDetailMapper.selectListByOrderId(id);

        // set shoppingCartList
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }

        // insert shoppingCartList
        shoppingCartMapper.insertList(shoppingCartList);
    }

    @Override
    public PageResult selectPageByCondition(OrderPageQueryDTO orderPageQueryDTO) {
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());

        // get orderList
        List<Order> orderList = orderMapper.selectPage(orderPageQueryDTO);

        // set orderVOList
        List<OrderVO> orderVOList = new ArrayList<>();
        if (orderList.isEmpty()) {
            return null;
        }
        for (Order order : orderList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);

            // set orderDetailList
            List<OrderDetail> orderDetailList = orderDetailMapper.selectListByOrderId(order.getId());
            orderVO.setOrderDetailList(orderDetailList);

            // set orderDishDescribeList
            List orderDishDescribeList = new ArrayList();
            for (OrderDetail orderDetail : orderDetailList) {
                orderDishDescribeList.add(orderDetail.getName() + "*" + orderDetail.getNumber() + ";");
            }
            orderVO.setOrderDishes(String.join("", orderDishDescribeList));

            orderVOList.add(orderVO);
        }

        return new PageResult(orderVOList.size(), orderVOList);
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(orderMapper.countStatus(Order.TO_BE_CONFIRMED));
        orderStatisticsVO.setConfirmed(orderMapper.countStatus(Order.CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress((orderMapper.countStatus(Order.DELIVERY_IN_PROGRESS)));
        return orderStatisticsVO;
    }

    @Override
    public OrderVO getOrderDetails(Long id) {
        Order order = orderMapper.selectById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        List<OrderDetail> orderDetailList = orderDetailMapper.selectListByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void confirmOrder(OrderConfirmDTO orderConfirmDTO) {
        Order order = new Order();
        order.setId(orderConfirmDTO.getId());
        order.setStatus(Order.CONFIRMED);
        orderMapper.update(order);
    }

    @Override
    public void rejectOrder(OrderRejectionDTO orderRejectionDTO) throws Exception {
        Order order = orderMapper.selectById(orderRejectionDTO.getId());

        if (order == null || !order.getStatus().equals(Order.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Integer payStatus = order.getPayStatus();

        // if it has been paid, then refund
        if (payStatus.equals(Order.PAID)) {
            weChatPayUtil.refund(order.getNumber(), order.getNumber(), new BigDecimal("0.01"), new BigDecimal("0.01"));
        }

        order.setStatus(Order.CANCELLED);
        order.setRejectionReason(orderRejectionDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    public void cancelOrder(OrderCancelDTO orderCancelDTO) throws Exception {
        Order order = orderMapper.selectById(orderCancelDTO.getId());

        // refund
        if (order.getPayStatus() == 1) {
            weChatPayUtil.refund(order.getNumber(), order.getNumber(), new BigDecimal("0.01"), new BigDecimal("0.01"));
        }

        order.setStatus(Order.CANCELLED);
        order.setCancelReason(orderCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());

        orderMapper.update(order);
    }

    @Override
    public void dispatchOrder(Long id) {
        Order order = orderMapper.selectById(id);

        if (order == null || !order.getStatus().equals(Order.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        order.setStatus(Order.DELIVERY_IN_PROGRESS);

        orderMapper.update(order);
    }

    @Override
    public void completeOrder(Long id) {
        Order order = orderMapper.selectById(id);

        if (order == null || !order.getStatus().equals(Order.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        order.setStatus(Order.COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(order);
    }
}