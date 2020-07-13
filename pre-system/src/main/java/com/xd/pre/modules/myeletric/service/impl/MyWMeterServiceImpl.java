package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterTenantTb;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeterRecord;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.dto.MyWMeterBasePriceDto;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyWMeterMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyWMeterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyWMeterServiceImpl  extends ServiceImpl<MyWMeterMapper, MyWMeter> implements IMyWMeterService {

    @Override
    public List<MyWMeter> getAllMeterList() {
        List<MyWMeter> list= baseMapper.getAllMeterList();
        return  list;
    }

    @Override
    public List<MyWMeter> getAreaWMeterList(Integer areaid) {
        return baseMapper.getAreaWMeterList(areaid);
    }


    @Override
    public List<MyWMeter> getMeterList(Integer roomid) {
        List<MyWMeter> list=baseMapper.getWMeterList(roomid);

        list.forEach(e->{
            System.out.print(e.toString());
        });

        return  list;
    }
    @Override
    public List<MyWMeter> getMeter(Integer meterid) {
        List<MyWMeter> list=baseMapper.getWMeter(meterid);

        return  list;
    }

    @Override
    public Integer bindMeter(MyMeterFilter meterFilter) {

        return  baseMapper.bindWMeter(meterFilter);

    }

    //解除电表和房间的绑定
    @Override
    public Integer unBindMeter(Integer meterid){

        return  baseMapper.unBindWMeter(meterid);

    }

    //设置水表的期初值和水价
    @Override
    public Integer updateWMeter(MyWMeterBasePriceDto wmeterbasedto){

        return  baseMapper.updateWMeter(wmeterbasedto);

    }

    //根据当前正在出租的所有水表
    public List<MyMeterTenantTb> getMeterTenatList()
    {
        return  baseMapper.getMeterTenatList();
    }

    //用水抄表
    public Integer recordWater(MyWMeter meter,MyWMeterRecord record)
    {
        try
        {
            meter.setWater_last(record.getWater_cur());
            baseMapper.recordWater(record);
            baseMapper.updateMeterLastWater(meter);
        }
        catch (Exception ex)
        {
            String s = ex.getMessage();
        }
        return 1;
    }

    //根据指定的条件查询用水记录
    public  List<MyWMeterRecord> getWaterRecord(MyMeterRdQryDto queryparam)
    {
        if (null == queryparam)
        {
            return  null;
        }

        return baseMapper.getWaterRecord(queryparam);
    }


}
