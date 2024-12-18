package com.example.bankmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bankmanager.conService.DepositInformationUser;
import com.example.bankmanager.conService.DepositManagerInformation;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface DepositMapper extends BaseMapper<DepositInformationUser> {
    List<DepositInformationUser> getAllflexToTurn(@Param("timenow") Timestamp timenow,@Param("depositid") String depositid);
    BigDecimal getTheBalance(@Param("depositid") String depositid,@Param("id") String id);

    int getDepositNum(@Param("depositid") String depositid);
    void UpdateDepositAdd(@Param("vdepositid") String vdepositid, @Param("vcardid") String vcardid,@Param("vamount") BigDecimal vamount);

    List<DepositInformationUser> getAllInflexToTurn (@Param("remark") String remark);


    void DeleteDeposit(@Param("vdepositid1") String vdepositid1,@Param("vdepositid2") String vdepositid2, @Param("vcardid") String vcardid,@Param("vamount") BigDecimal vamount);

}
