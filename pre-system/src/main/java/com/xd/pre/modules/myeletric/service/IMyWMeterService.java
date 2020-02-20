package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;

import java.util.List;

public interface IMyWMeterService  extends IService<MyWMeter> {

    List<MyWMeter> getMeterList(Integer roomid);
    List<MyWMeter> getMeter(Integer meterid);
    Integer bindMeter(MyMeterFilter meterFilter);
    Integer unBindMeter(Integer meterid);


}
