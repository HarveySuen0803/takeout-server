package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate);

    UserReportVO getUserStatistics(LocalDate beginDate, LocalDate endDate);

    OrderReportVO getOrderStatistics(LocalDate beginDate, LocalDate endDate);

    SalesTop10ReportVO getSalesTop(LocalDate beginDate, LocalDate endDate);
}
