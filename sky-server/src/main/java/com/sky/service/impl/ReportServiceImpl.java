package com.sky.service.impl;

import com.sky.entity.Order;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.TrustAnchor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        // set dateList
        List<LocalDate> dateList = new ArrayList<>();
        while (!beginDate.equals(endDate)) {
            dateList.add(beginDate);
            beginDate = beginDate.plusDays(1);
        }

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
}
