package com.example.bankmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bankmanager.conService.AccountInformation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper extends BaseMapper<AccountInformation> {
    List<AccountInformation> getAll();
    void PutAccountState(@Param("cardid") String cardid,@Param("accountstate")String accountstate);

    int getSum();
    void PutAccountPassWord(@Param("cardid") String cardid,@Param("password")String password);


}
