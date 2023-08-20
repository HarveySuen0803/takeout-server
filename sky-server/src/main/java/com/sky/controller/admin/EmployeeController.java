package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("Employee login, param is {}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    public Result<String> insert(@RequestBody EmployeeDTO employeeDTO) {
        log.info("insert Employee, param is {}", employeeDTO);
        employeeService.insert(employeeDTO);
        return Result.success();
    }

    @PutMapping
    public Result<String> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("update Employee, param is {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> selectByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("select Employee by page, param is {}", employeePageQueryDTO);
        PageResult pageResult = employeeService.selectByPage(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatusById(Long id, @PathVariable Integer status) {
        log.info("update Employee status, param is (id={}, status={})", id, status);
        employeeService.changeStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Employee> selectById(@PathVariable Long id) {
        log.info("select Employee by id, param is {}", id);
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }
}
