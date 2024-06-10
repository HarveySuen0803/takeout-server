package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrderPageQueryDTO;
import com.sky.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Order order);

    List<Order> selectPage(OrderPageQueryDTO orderPageQueryDTO);

    @Select("select * from `order` where id = #{id}")
    Order selectById(Long id);

    void update(Order order);

    @Select("select count(id) from `order` where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);

    @Select("select * from `order` where status = #{status} and order_time < #{time}")
    List<Order> selectByStatusAndOrderTime(Integer status, LocalDateTime time);

    Double sumAmountByCondition(Map orderMap);

    Integer countIdByCondition(Map orderMap);

    List<GoodsSalesDTO> getSalesTop(LocalDateTime beginDateTime, LocalDateTime endDateTime);
}
