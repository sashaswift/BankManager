package com.example.bankmanager.controller;

import com.example.bankmanager.conService.AccountInformation;
import com.example.bankmanager.conService.DepositInformationUser;
import com.example.bankmanager.conService.DepositManagerInformation;
import com.example.bankmanager.conService.EmployeeInformation;
import com.example.bankmanager.mapper.AccountMapper;
import com.example.bankmanager.mapper.DepositManagerMapper;
import com.example.bankmanager.mapper.DepositMapper;
import com.example.bankmanager.mapper.EmployeeMapper;
import com.example.bankmanager.service.*;
import com.example.bankmanager.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@Component
@RequestMapping("/manager")
public class managerController {
    @Autowired
    private DepositMapper depositMapper;

    @Autowired
    private DepositManagerMapper depositManagerMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private IPoiDepositServcie poiDepositServcie;

    @Autowired
    private IPoiEmployeeService poiEmployeeService;

    @Autowired
    private IPoiDepositManagerService poiDepositManagerService;

    @Autowired
    private IPoiAccountService poiAccountService;

    @GetMapping("/getallaccount")
    public Result GetAllAccount(){
        List<AccountInformation> accountInformations=accountMapper.getAll();
        List<AccountVo> accountVos=new ArrayList<>();

        for(AccountInformation accountInformation:accountInformations){
            AccountVo accountVo=new AccountVo();
            BeanUtils.copyProperties(accountInformation,accountVo);
            accountVos.add(accountVo);
        }
        return Result.success(accountVos);
    }

    @GetMapping("/getaccountnum")
    public Result GetAccountNum(){
        int sum=accountMapper.getSum();
        return Result.success(sum);
    }

    @PutMapping("/putaccountstate")
    public Result PutAccountState(String cardid, String accountstate){
        accountMapper.PutAccountState(cardid,accountstate);
        AccountInformation accountInformation= accountMapper.selectById(cardid);
        AccountVo accountVo=new AccountVo();
        BeanUtils.copyProperties(accountInformation,accountVo);
        return Result.success(accountVo);
    }

    @PutMapping("/putaccountpassword")
    public Result PutAccountPassword(String cardid,String password){
        accountMapper.PutAccountPassWord(cardid,password);
        AccountInformation accountInformation= accountMapper.selectById(cardid);
        AccountVo accountVo=new AccountVo();
        BeanUtils.copyProperties(accountInformation,accountVo);
        return Result.success(accountVo);
    }

    @GetMapping("/getalldepositmanager")
    public Result GetAllDepositManager (){
        List<DepositManagerInformation> depositManagerInformations=depositManagerMapper.getAll();
        List<DepositManageVo> depositManageVos=new ArrayList<>();
        for(DepositManagerInformation depositManagerInformation:depositManagerInformations){
            DepositManageVo depositManageVo=new DepositManageVo();
            BeanUtils.copyProperties(depositManagerInformation,depositManageVo);
            depositManageVos.add(depositManageVo);
        }
        return Result.success(depositManageVos);
    }

    @PostMapping("/adddepositmanager")
    public Result AddDepositManager(@RequestBody DepositManagerInformation depositManagerInformation){
        int sum=depositManagerMapper.getSum();
        depositManagerInformation.depositid=String.format("%05d",sum);
        poiDepositManagerService.save(depositManagerInformation);
        DepositManagerInformation depositManagerInformation1=poiDepositManagerService.getById(depositManagerInformation.depositid);
        DepositManageVo depositManageVo=new DepositManageVo();
        BeanUtils.copyProperties(depositManagerInformation1,depositManageVo);
        return Result.success(depositManageVo);
    }

    @PutMapping("/changedepositmanager")
    public Result ChangeDepositManager(@RequestBody DepositManagerInformation depositManagerInformation){
        poiDepositManagerService.updateById(depositManagerInformation);
        DepositManagerInformation depositManagerInformation1=poiDepositManagerService.getById(depositManagerInformation.depositid);
        DepositManageVo depositManageVo=new DepositManageVo();
        BeanUtils.copyProperties(depositManagerInformation1,depositManageVo);
        return Result.success(depositManageVo);

    }

    @DeleteMapping("/deletedepositmanager")
    public Result DeleteDepositManager(String depositid){
        int num=depositMapper.getDepositNum(depositid);
        if(num>0) return Result.fail("仍有存款存在，无法删除!");
        depositManagerMapper.deleteDepositManager(depositid);
        return Result.success();
    }

    @GetMapping("/getdepositnum")
    public Result GetDepositNum(String depositid){
        int num=depositMapper.getDepositNum(depositid);
        return Result.success(num);
    }

    @PutMapping("/updatethedayprofit")
    public Result UpdateTheDayProfit(){
        List<DepositInformationUser> depositInformationUsers=depositMapper.getAllInflexToTurn("活期存款");
        List<DepositVo> depositeVos=new ArrayList<>();
        for(DepositInformationUser depositInformationUser:depositInformationUsers){
            BigDecimal profit=depositMapper.getTheBalance(depositInformationUser.depositid,depositInformationUser.id);
            depositMapper.UpdateDepositAdd(depositInformationUser.depositid,depositInformationUser.cardid,profit);
            //DepositInformationUser depositInformationUser1=poiDepositServcie.getById(depositInformationUser.id);
            DepositVo depositVo=new DepositVo();
            BeanUtils.copyProperties(poiDepositServcie.getById(depositInformationUser.id),depositVo);
            depositeVos.add(depositVo);
        }
        return Result.success(depositeVos);
    }

    @PutMapping("/flextoinflex")
    public Result FlexToInflex(){
        LocalDateTime now = LocalDateTime.now();
        List<DepositManagerInformation> depositManagerInformations=depositManagerMapper.getFlexAll();
        String vdeposit2=depositManagerMapper.GetByRemark("活期存款").get(0);
        for(DepositManagerInformation depositManagerInformation:depositManagerInformations){
             now.minus(depositManagerInformation.limityear.longValue(), ChronoUnit.YEARS);
            Timestamp timenow = Timestamp.from(now.atZone(ZoneOffset.UTC).toInstant());
             List<DepositInformationUser> depositInformationUsers=depositMapper.getAllflexToTurn(timenow,depositManagerInformation.depositid);
             for(DepositInformationUser depositInformationUser:depositInformationUsers){
                 depositMapper.DeleteDeposit(depositInformationUser.depositid,vdeposit2,depositInformationUser.cardid,depositInformationUser.depositbalance);
             }
        }
        return Result.success();
    }

    @GetMapping("/getallemployee")
    public Result GetAllEmployee(){
        List<EmployeeInformation> employeeInformations=employeeMapper.getAll();
        List<EmployeeVo> employeeVos=new ArrayList<>();
        for(EmployeeInformation employeeInformation:employeeInformations){
            EmployeeVo employeeVo=new EmployeeVo();
            BeanUtils.copyProperties(employeeInformation,employeeVo);
            employeeVos.add(employeeVo);
        }
        return Result.success(employeeVos);
    }

    @GetMapping("/getemployeebyid")
    public Result GetEmployeeByID(String id){
        EmployeeInformation employeeInformation= poiEmployeeService.getById(id);
        EmployeeVo employeeVo=new EmployeeVo();
        BeanUtils.copyProperties(employeeInformation,employeeVo);
        return Result.success(employeeVo);
    }

    @PostMapping("/addemployee")
    public Result AddEmployee(@RequestBody EmployeeInformation employeeInformation){
        String employeeid=String.format("%07d",employeeMapper.getEmployeeNumber());
        employeeInformation.setEmployeeid(employeeid);
        poiEmployeeService.save(employeeInformation);
        EmployeeInformation employeeInformation1= poiEmployeeService.getById(employeeid);
        EmployeeVo employeeVo=new EmployeeVo();
        BeanUtils.copyProperties(employeeInformation1,employeeVo);
        return Result.success(employeeVo);
    }

    @PutMapping("/changeemployee")
    public Result ChangeEmployee(@RequestBody EmployeeInformation employeeInformation){
        poiEmployeeService.updateById(employeeInformation);
        EmployeeInformation employeeInformation1= poiEmployeeService.getById(employeeInformation.employeeid);
        EmployeeVo employeeVo=new EmployeeVo();
        BeanUtils.copyProperties(employeeInformation1,employeeVo);
        return Result.success(employeeVo);
    }

    @DeleteMapping("/deleteemployee")
    public Result DeleteEmployee(String id){
         employeeMapper.DeleteById(id);
         EmployeeInformation employeeInformation=poiEmployeeService.getById(id);
         if(employeeInformation!=null) return Result.fail("删除失败");
         return Result.success();
    }

    @Autowired
    private BackupService backupService;

    @Autowired
    private RestoreService restoreService;


    @PostMapping("/startbackup")
    public Result StartBackUp(){
        try {

            Path file = this.backupService.backup();
            //employeeMapper.startBackUp();
            return Result.success(file.toAbsolutePath().toString());

        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    @PutMapping("/rollback")
    public Result RollBack(){
        try {

            this.restoreService.restoreDatabase();

            return Result.success();

        } catch (Exception e) {
            return Result.fail( e.getMessage());
        }
    }
}
