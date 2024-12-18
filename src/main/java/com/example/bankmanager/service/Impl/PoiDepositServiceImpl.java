package com.example.bankmanager.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bankmanager.conService.DepositInformationUser;
import com.example.bankmanager.mapper.DepositMapper;
import com.example.bankmanager.service.IPoiDepositServcie;
import org.springframework.stereotype.Service;

@Service
public class PoiDepositServiceImpl extends ServiceImpl<DepositMapper, DepositInformationUser> implements IPoiDepositServcie {
}
