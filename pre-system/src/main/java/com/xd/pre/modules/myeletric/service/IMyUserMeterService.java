package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import org.apache.ibatis.annotations.Param;

public interface IMyUserMeterService  extends IService<MyUserMeter>{

    //获取电表的所有权映射表
     MyUserMeter getUserMeterByMeterid(Integer meterid);
     Integer addNewUserMeter(MyUserMeter meteruser);
}
