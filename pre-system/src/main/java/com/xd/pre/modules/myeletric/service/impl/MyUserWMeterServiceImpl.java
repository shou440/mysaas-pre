package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.domain.MyUserWMeter;
import com.xd.pre.modules.myeletric.mapper.MyUserMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyUserWMeterMapper;
import com.xd.pre.modules.myeletric.service.IMyUserMeterService;
import com.xd.pre.modules.myeletric.service.IMyUserWMeterService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MyUserWMeterServiceImpl extends ServiceImpl<MyUserWMeterMapper, MyUserWMeter> implements IMyUserWMeterService {


    @Override
    public MyUserWMeter getUserMeterByMeterid(Integer meterid)
    {
        List<MyUserWMeter> lst = baseMapper.getWUserMeterByMeterid(meterid);
        if (null == lst || lst.size() == 0)
        {
            return  null;
        }
        return  lst.get(0);
    }
}
