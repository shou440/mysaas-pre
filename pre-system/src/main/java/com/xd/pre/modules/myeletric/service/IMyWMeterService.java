package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterTenantTb;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeterRecord;
import com.xd.pre.modules.myeletric.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IMyWMeterService  extends IService<MyWMeter> {

    List<MyWMeter> getAllMeterList();
    List<MyWMeter> getAreaWMeterList(Integer areaid);
    List<MyWMeter> getMeterList(Integer roomid);
    List<MyWMeter> getMeter(Integer meterid);

    Integer bindMeter(MyMeterFilter meterFilter);
    Integer unBindMeter(Integer meterid);
    Integer updateWMeter(MyWMeterBasePriceDto wmeterbasedto);

    List<MyMeterTenantTb> getMeterTenatList();
    public Integer recordWater(MyWMeter meter,MyWMeterRecord record);
    List<MyWMeterRecord> getWaterRecord(MyMeterRdQryDto queryparam);


}
