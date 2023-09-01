package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.impl.ReportServiceImpl;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        log.info("turnover statistics, beginTime: {}, endTime: {}", beginDate, endDate);
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(beginDate, endDate);
        return Result.success(turnoverReportVO);
    }
}
