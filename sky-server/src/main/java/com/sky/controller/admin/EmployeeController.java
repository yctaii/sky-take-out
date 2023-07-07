package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
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
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;


    /**
     * @param employeeLoginDTO:
     * @return Result<EmployeeLoginVO>
     * @author richard
     * @description 实现员工登陆
     * @date 2023/7/6 16:42
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);



        Employee employee = employeeService.login(employeeLoginDTO);

        //用ThreadLocal存储当前登陆用户id，方便后面获取
        BaseContext.setCurrentId(employee.getId());

        //登录成功后，生成jwt令牌
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

    /**
     * @param :
     * @return Result<String>
     * @author richard
     * @description 员工退出
     * @date 2023/7/6 16:43
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping()
    @ApiOperation("新增员工")
    public Result<String> addEmp(@RequestBody EmployeeDTO employeeDTO){

        if(employeeDTO == null) return Result.error("新增员工信息不能为空");
        employeeService.addEmp(employeeDTO);

        return Result.success("添加成功");
    }

    @GetMapping("/page")
    @ApiOperation("员工信息分页查询")
    public Result<PageResult> getPage(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("开始分页查询。。。");
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }
}
