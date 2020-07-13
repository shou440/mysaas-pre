package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.*;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.dto.MyWMeterBasePriceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyWMeterMapper extends BaseMapper<MyWMeter> {

    //获取系统所有的电表
    public List<MyWMeter> getAllMeterList();

    //获取房间的所有水表
    public List<MyWMeter> getAreaWMeterList(@Param("areaid") Integer areaid);

    //获取房间的所有水表
    public List<MyWMeter> getWMeterList(@Param("roomid") Integer roomid);

    //根据水表号获取水表
    public List<MyWMeter> getWMeter(@Param("meterid") Integer meterid);

    //绑定水表到指定的房间
    public Integer bindWMeter(@Param("filter") MyMeterFilter filter);

    //解绑水表
    public Integer unBindWMeter(@Param("meterid") Integer meterid);

    //水表水价和期初值参数
    public Integer updateWMeter(@Param("wmeterbaseprice") MyWMeterBasePriceDto wmeterbaseprice);

    //获取所有指定合同的所有水表
    public List<MyMeterTenantTb> getMeterTenatList();

    //更新电表上一期参数，主要用于保存电度后更新上一次的读数
    public Integer updateMeterLastWater(@Param("meter") MyWMeter meter);

    //抄表记录到系统中
    public Integer recordWater(@Param("record") MyWMeterRecord record);

    //获取起始的用电记录
    public List<MyWMeterRecord> getWaterRecord(@Param("queryparam") MyMeterRdQryDto queryparam);

}
