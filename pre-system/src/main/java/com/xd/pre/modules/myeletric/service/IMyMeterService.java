package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;


import java.util.List;

public interface IMyMeterService  extends IService<MyMeter> {

    List<MyMeter> getMeterList(Integer roomid);
    List<MyMeter> getMeter(Integer meterid);
    Integer bindMeter(MyMeterFilter meterFilter);
    Integer unBindMeter(Integer meterid);
}
