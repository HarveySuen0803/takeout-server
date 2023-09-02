package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Order;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        // set dateList
        List<LocalDate> dateList = getDateList(beginDate, endDate);

        // set turnoverList
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("beginDateTime", beginDateTime);
            map.put("endDateTime", endDateTime);
            map.put("status", Order.COMPLETED);

            Double turnover = orderMapper.sumAmountByCondition(map);

            if (turnover == null) {
                turnover = 0.0;
            }

            turnoverList.add(turnover);
        }

        // set turnoverReportVO
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(dateList, ","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate beginDate, LocalDate endDate) {
        // set dateList
        List<LocalDate> dateList = getDateList(beginDate, endDate);

        // set newUserCountList and totalUserCountList
        List<Integer> newUserCountList = new ArrayList<>();
        List<Integer> totalUserCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            // set newUserCount
            Map map = new HashMap();
            map.put("beginDateTime", null);
            map.put("endDateTime", endDateTime);
            Integer totalUserCount = userMapper.countIdByTime(map);

            // set totalUserCount
            map.put("beginDateTime", beginDateTime);
            Integer newUserCount = userMapper.countIdByTime(map);

            newUserCountList.add(newUserCount);
            totalUserCountList.add(totalUserCount);
        }

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(StringUtils.join(dateList, ","));
        userReportVO.setNewUserList(StringUtils.join(newUserCountList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserCountList, ","));

        return userReportVO;
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate beginDate, LocalDate endDate) {
        // set dateList
        List<LocalDate> dateList = getDateList(beginDate, endDate);

        // set orderCountList and validOrderCountList
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            // set orderCount
            Map map = new HashMap();
            map.put("beginDateTime", beginDateTime);
            map.put("endDateTime", endDateTime);
            Integer orderCount = orderMapper.countIdByCondition(map);
            orderCountList.add(orderCount);

            // set validOrderCount
            map.put("status", Order.COMPLETED);
            Integer validOrderCount = orderMapper.countIdByCondition(map);
            validOrderCountList.add(validOrderCount);
        }

        // count total order
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        // count total valid order
        Integer totalValidOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // set order completion rate
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = totalValidOrderCount.doubleValue() / totalOrderCount;
        }

        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(StringUtils.join(dateList, ","));
        orderReportVO.setOrderCountList(StringUtils.join(orderCountList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList, ","));
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(totalValidOrderCount);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);

        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getSalesTop(LocalDate beginDate, LocalDate endDate) {
        LocalDateTime beginDateTime = LocalDateTime.of(beginDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        // get salesTopList
        List<GoodsSalesDTO> salesTopList = orderMapper.getSalesTop(beginDateTime, endDateTime);

        // get nameList
        List<String> nameList = salesTopList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());

        // get numberList
        List<Integer> numberList = salesTopList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(StringUtils.join(nameList, ","));
        salesTop10ReportVO.setNumberList(StringUtils.join(numberList, ","));

        return salesTop10ReportVO;
    }

    public List<LocalDate> getDateList(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!beginDate.equals(endDate)) {
            dateList.add(beginDate);
            beginDate = beginDate.plusDays(1);
        }
        return dateList;
    }
}
