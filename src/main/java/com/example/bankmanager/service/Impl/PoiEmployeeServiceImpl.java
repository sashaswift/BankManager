package com.example.bankmanager.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bankmanager.conService.EmployeeInformation;
import com.example.bankmanager.mapper.EmployeeMapper;
import com.example.bankmanager.service.IPoiEmployeeService;
import org.springframework.stereotype.Service;

@Service
public class PoiEmployeeServiceImpl extends ServiceImpl<EmployeeMapper, EmployeeInformation> implements IPoiEmployeeService {
}
