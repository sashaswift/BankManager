package com.example.bankmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bankmanager.conService.EmployeeInformation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeMapper extends BaseMapper<EmployeeInformation> {
    List<EmployeeInformation> getAll();
    int getEmployeeNumber();

    void startBackUp();

    void startRollBack();
    void DeleteById(@Param("employeeid") String employeeid);
}
