package com.example.bankmanager.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bankmanager.conService.DepositManagerInformation;
import com.example.bankmanager.mapper.DepositManagerMapper;
import com.example.bankmanager.service.IPoiDepositManagerService;
import org.springframework.stereotype.Service;

@Service
public class PoiDepositManagerServiceImpl extends ServiceImpl<DepositManagerMapper, DepositManagerInformation> implements IPoiDepositManagerService{
}
