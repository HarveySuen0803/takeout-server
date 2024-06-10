package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.vo.EmployeeLoginVO;

public interface EmployeeService {
    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    void insert(EmployeeDTO employeeDTO);

    PageResult selectByPage(EmployeePageQueryDTO employeePageQueryDTO);

    void updateStatusById(Long id, Integer status);

    Employee selectById(Long id);

    void update(EmployeeDTO employeeDTO);
}
