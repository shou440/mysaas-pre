package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyWMeterMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyWMeterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyWMeterServiceImpl  extends ServiceImpl<MyWMeterMapper, MyWMeter> implements IMyWMeterService {

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

}
