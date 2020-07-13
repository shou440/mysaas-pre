package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterRecord;
import com.xd.pre.modules.myeletric.domain.MyMeterTenantTb;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.vo.MyMeterFeeVo;
import org.apache.ibatis.annotations.Param;


import java.sql.Timestamp;
import java.util.List;

public interface IMyMeterService  extends IService<MyMeter> {

    List<MyMeter> getAreaMeterList(Integer areaid);
    List<MyMeter> getAllMeterList();
    List<MyMeter> getMeterList(Integer roomid);
    List<MyMeter> getMeter(Integer meterid);
    List<MyMeter> getMeterByTenantOpenid(String openid);
    Integer bindMeter(MyMeterFilter meterFilter);
    Integer unBindMeter(Integer meterid);
    Integer updateMeter(MyMeterBasePriceDto meterbasedto);
     List<MyMeterTenantTb> getMeterTenatList();
    Integer saveEP(MyMeter meter,MyMeterRecord record);
    List<MyMeterRecord> getEpRecord(MyMeterRdQryDto qrParam);


}
