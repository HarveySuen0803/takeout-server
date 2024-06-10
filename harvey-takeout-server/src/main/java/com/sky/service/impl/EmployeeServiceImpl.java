package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private EmployeeMapper employeeMapper;

    // Feat: Login
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        // get employee
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        Employee employee = employeeMapper.selectByUsername(username);

        // if employee does not exist, throw Exception
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // if password is incorrect, throw Exception
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // if employee is disabled, throw Exception
        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // create token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

        EmployeeLoginVO employeeLoginVO = new EmployeeLoginVO();
        employeeLoginVO.setId(employee.getId());
        employeeLoginVO.setUserName(employee.getUsername());
        employeeLoginVO.setName(employee.getName());
        employeeLoginVO.setToken(token);

        return employeeLoginVO;
    }

    @Override
    public void insert(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeeMapper.insert(employee);
    }

    @Override
    public PageResult selectByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = (Page<Employee>) employeeMapper.selectByPage(employeePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatusById(Long id, Integer status) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
        employeeMapper.update(employee);
    }

    @Override
    public Employee selectById(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }
}
