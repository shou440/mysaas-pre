package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;
import com.xd.pre.modules.myeletric.mapper.MyUserMeterMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import com.xd.pre.modules.myeletric.service.IMyUserMeterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserMeterServiceImpl  extends ServiceImpl<MyUserMeterMapper, MyUserMeter> implements IMyUserMeterService
{

    @Override
    public MyUserMeter getUserMeterByMeterid(Integer meterid)
    {
        List<MyUserMeter> lst = baseMapper.getUserMeterByMeterid(meterid);
        if (null == lst || lst.size() == 0)
        {
            return  null;
        }
        return  lst.get(0);
    }
}
