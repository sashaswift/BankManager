package com.example.bankmanager.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bankmanager.conService.AccountInformation;
import com.example.bankmanager.mapper.AccountMapper;
import com.example.bankmanager.service.IPoiAccountService;
import org.springframework.stereotype.Service;

import java.security.Provider;

@Service
public class PoiAccountServiceImpl extends ServiceImpl<AccountMapper, AccountInformation> implements IPoiAccountService {
}
