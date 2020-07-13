package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterRecord;
import com.xd.pre.modules.myeletric.domain.MyMeterTenantTb;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.dto.MyMeterRdQryDto;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MyMeterServiceImpl extends ServiceImpl<MyMeterMapper, MyMeter> implements IMyMeterService {


    @Override
    public List<MyMeter> getAreaMeterList(Integer areaid) {
        List<MyMeter> list= baseMapper.getAreaMeterList(areaid);
        return  list;
    }

    @Override
    public List<MyMeter> getAllMeterList() {
        List<MyMeter> list= baseMapper.getAllMeterList();
        return  list;
    }

    @Override
    public List<MyMeter> getMeterList(Integer roomid) {
        List<MyMeter> list=baseMapper.getMeterList(roomid);

        list.forEach(e->{
            System.out.print(e.toString());
        });

        return  list;
    }
    @Override
    public List<MyMeter> getMeter(Integer meterid) {
        List<MyMeter> list=baseMapper.getMeter(meterid);

        return  list;
    }

    @Override
    public List<MyMeter> getMeterByTenantOpenid(String openid) {
        return baseMapper.getMeterByTenantOpenid(openid);
    }

    @Override
    public Integer bindMeter(MyMeterFilter meterFilter) {

        return  baseMapper.bindMeter(meterFilter);

    }

    //解除电表和房间的绑定
    @Override
    public Integer unBindMeter(Integer meterid){

        return  baseMapper.unBindMeter(meterid);

    }

    //设置电表的期初值和电价
    @Override
    public Integer updateMeter(MyMeterBasePriceDto meterbasedto){

        return  baseMapper.updateMeter(meterbasedto);

    }

    //设置电表的期初值和电价
    @Override
    public List<MyMeterTenantTb> getMeterTenatList(){

        return  baseMapper.getMeterTenatList();

    }

    //保存抄表，采用事务处理方式，保存记录并修改电表的上一次读数
    @Transactional
    @Override
    public Integer saveEP(MyMeter meter,MyMeterRecord record)
    {
        try
        {
            meter.setEp_last(record.getEp_cur());
            baseMapper.recordEp(record);
           // baseMapper.updateMeterLastEp(meter);
        }
        catch (Exception ex)
        {
            String s = ex.getMessage();
        }
        return 1;
    }



    //获取电表一段时间内的累计电度
    @Override
    public List<MyMeterRecord> getEpRecord(MyMeterRdQryDto qrParam)
    {

        if (null == qrParam)
        {
            return  null;
        }

        return baseMapper.getEpRecord(qrParam);

    }

}
