package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    TurnoverReportVO turnoverStatistics(LocalDate beginDate, LocalDate endDate);

    UserReportVO userStatistics(LocalDate beginDate, LocalDate endDate);
}
