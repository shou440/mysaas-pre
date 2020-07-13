package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyMeterMapper  extends BaseMapper<MyMeter> {

    //获取系统所有的电表
    public List<MyMeter> getAllMeterList();

    //获取园区的所有电表
    public List<MyMeter> getAreaMeterList(@Param("areaid") Integer areaid);

    //获取房间的所有电表
    public List<MyMeter> getMeterList(@Param("roomid") Integer roomid);

    //根据电表号获取电表
    public List<MyMeter> getMeter(@Param("meterid") Integer meterid);

    //根据租户的openid获取租户的电表
    public List<MyMeter> getMeterByTenantOpenid(@Param("openid") String openid);


    //绑定电表到指定的房间
    public Integer bindMeter(@Param("filter") MyMeterFilter filter);

    //解绑电表
    public Integer unBindMeter(@Param("meterid") Integer meterid);

    //电表电价和期初值参数
    public Integer updateMeter(@Param("meterbaseprice") MyMeterBasePriceDto meterbaseprice);

    //获取所有电表的流水和合同号
    public List<MyMeterTenantTb> getMeterTenatList();

    //更新电表上一期参数，主要用于保存电度后更新上一次的读数
    public Integer updateMeterLastEp(@Param("meter") MyMeter meter);

    //抄表记录到系统中
    public Integer recordEp(@Param("record") MyMeterRecord record);

    //获取起始的用电记录
    public List<MyMeterRecord> getEpRecord(@Param("queryparam") MyMeterRdQryDto queryparam);

}
