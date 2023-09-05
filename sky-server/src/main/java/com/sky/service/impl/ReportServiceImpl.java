package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Order;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.standard.DateTimeFormatterFactoryBean;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.poi.ss.util.SheetUtil.getCell;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WorkspaceService workspaceService;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        // get the list containing days from begin-date to end-date
        List<LocalDate> dateList = getDateList(beginDate, endDate);

        // get list containing turnover
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            // get turnover
            Map orderMap = new HashMap();
            orderMap.put("beginDateTime", beginDateTime);
            orderMap.put("endDateTime", endDateTime);
            orderMap.put("status", Order.COMPLETED);
            Double turnover = orderMapper.sumAmountByCondition(orderMap);

            if (turnover == null) {
                turnover = 0.0;
            }

            turnoverList.add(turnover);
        }

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(dateList, ","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> dateList = getDateList(beginDate, endDate);

        // get the list containing the number of new user
        List<Integer> newUserCountList = new ArrayList<>();

        // get the list containing the number of total user
        List<Integer> totalUserCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            // get the number of new user
            Map userMap = new HashMap();
            userMap.put("beginDateTime", beginDateTime);
            userMap.put("endDateTime", endDateTime);
            Integer newUserCount = userMapper.countIdByCondition(userMap);

            // get the number of total user
            userMap.put("beginDateTime", null);
            Integer totalUserCount = userMapper.countIdByCondition(userMap);

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
        // get the list containing days from begin-date to end-date
        List<LocalDate> dateList = getDateList(beginDate, endDate);

        // get the list containing the number of order
        List<Integer> orderCountList = new ArrayList<>();

        // get the list containing the number of valid order
        List<Integer> validOrderCountList = new ArrayList<>();

        // get orderCountList and validOrderCountList
        for (LocalDate date : dateList) {
            LocalDateTime beginDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            // get the number of order
            Map orderMap = new HashMap();
            orderMap.put("beginDateTime", beginDateTime);
            orderMap.put("endDateTime", endDateTime);
            Integer orderCount = orderMapper.countIdByCondition(orderMap);
            orderCountList.add(orderCount);

            // get the number of the valid order
            orderMap.put("status", Order.COMPLETED);
            Integer validOrderCount = orderMapper.countIdByCondition(orderMap);
            validOrderCountList.add(validOrderCount);
        }

        // get the number of total order
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        // get the number of total valid order
        Integer totalValidOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // get the order completion rate
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

        // get the list containing the ranking of sales
        List<GoodsSalesDTO> salesTopList = orderMapper.getSalesTop(beginDateTime, endDateTime);

        List<String> nameList = salesTopList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());

        List<Integer> numberList = salesTopList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(StringUtils.join(nameList, ","));
        salesTop10ReportVO.setNumberList(StringUtils.join(numberList, ","));

        return salesTop10ReportVO;
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDateTime beginDateTime = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);

        BusinessDataVO businessDataForMonth = workspaceService.getBusinessData(beginDateTime, endDateTime);

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/business-data.xlsx");
            XSSFWorkbook excel = new XSSFWorkbook(is);
            XSSFSheet sheet = excel.getSheet("sheet1");

            XSSFRow row1 = sheet.getRow(1);
            row1.getCell(1).setCellValue("time: " + beginDateTime + " - " + endDateTime);

            XSSFRow row3 = sheet.getRow(3);
            row3.getCell(2).setCellValue(businessDataForMonth.getTurnover());
            row3.getCell(4).setCellValue(businessDataForMonth.getOrderCompletionRate());
            row3.getCell(6).setCellValue(businessDataForMonth.getNewUsers());

            XSSFRow row5 = sheet.getRow(5);
            row5.getCell(2).setCellValue(businessDataForMonth.getValidOrderCount());
            row5.getCell(4).setCellValue(businessDataForMonth.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                beginDateTime = beginDateTime.plusDays(1);
                BusinessDataVO businessDataForDay = workspaceService.getBusinessData(beginDateTime.with(LocalTime.MIN), beginDateTime.with(LocalTime.MAX));
                XSSFRow row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(beginDateTime.toString());
                row.getCell(2).setCellValue(businessDataForDay.getTurnover());
                row.getCell(3).setCellValue(businessDataForDay.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataForDay.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataForDay.getUnitPrice());
                row.getCell(6).setCellValue(businessDataForDay.getNewUsers());
            }

            ServletOutputStream os = response.getOutputStream();
            excel.write(os);

            is.close();
            os.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
