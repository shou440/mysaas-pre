package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyMeterServiceImpl extends ServiceImpl<MyMeterMapper, MyMeter> implements IMyMeterService {


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
    public Integer bindMeter(MyMeterFilter meterFilter) {

        return  baseMapper.bindMeter(meterFilter);

    }

    //解除电表和房间的绑定
    @Override
    public Integer unBindMeter(Integer meterid){

        return  baseMapper.unBindMeter(meterid);

    }



}
