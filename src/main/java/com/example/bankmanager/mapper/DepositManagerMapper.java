package com.example.bankmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bankmanager.conService.DepositManagerInformation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepositManagerMapper extends BaseMapper<DepositManagerInformation> {
    List<DepositManagerInformation> getAll();
    int getSum();
    List<DepositManagerInformation> getFlexAll();
    List<String> GetByRemark(@Param("remark") String remark);
    void deleteDepositManager(@Param("depositid") String depositid);

}
