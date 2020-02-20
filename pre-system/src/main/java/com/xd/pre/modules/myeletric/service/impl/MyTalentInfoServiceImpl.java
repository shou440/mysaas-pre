package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyTalentInfo;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyTalentInfoMapper;
import com.xd.pre.modules.myeletric.service.IMyMeterService;
import com.xd.pre.modules.myeletric.service.IMyTalentInfoService;
import org.springframework.stereotype.Service;

import java.util.List;


//租户对象
@Service
public class MyTalentInfoServiceImpl extends ServiceImpl<MyTalentInfoMapper, MyTalentInfo> implements IMyTalentInfoService {

    @Override
    public MyTalentInfo getTanlentInfo(Integer roomid) {

        List<MyTalentInfo> list =   baseMapper.getTanlentInfo(roomid);
        if (null == list || list.size() == 0)
        {
            return  null;
        }
        return list.get(0);
    }
}
