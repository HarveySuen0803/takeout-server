package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> getTurnoverStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        log.info("get turnover statistics, beginDate: {}, endDate: {}", beginDate, endDate);
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(beginDate, endDate);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        log.info("get user statistics, beginDate: {}, endDate: {}", beginDate, endDate);
        UserReportVO userReportVO = reportService.getUserStatistics(beginDate, endDate);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrderStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        log.info("get user statistics, beginDate: {}, endDate: {}", beginDate, endDate);
        OrderReportVO orderReportVO = reportService.getOrderStatistics(beginDate, endDate);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getSalesTop(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        log.info("get sales top, beginDate: {}, endDate: {}", beginDate, endDate);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop(beginDate, endDate);
        return Result.success(salesTop10ReportVO);
    }

    @GetMapping("/export")
    public Result<String> exportBusinessData(HttpServletResponse response) {
        log.info("export business data");
        reportService.exportBusinessData(response);
        return Result.success();
    }
}
