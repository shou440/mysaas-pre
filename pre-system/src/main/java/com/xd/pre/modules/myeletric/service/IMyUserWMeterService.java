package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyUserWMeter;

public interface IMyUserWMeterService extends IService<MyUserWMeter> {

    //获取电表的所有权映射表
    MyUserWMeter getUserMeterByMeterid(Integer meterid);
}
