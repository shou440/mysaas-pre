package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.mapper.MyAreaMapper;
import com.xd.pre.modules.myeletric.service.IMyAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyAreaServiceImpl  extends ServiceImpl<MyAreaMapper, MyArea> implements IMyAreaService {

    @Override
    public List<MyArea> getAreaByOwnerOpenid(Integer userid){

        List<MyArea> list=baseMapper.getAreaByOwner(userid);
        list.forEach(e->{
            System.out.print(e.toString());
        });
        return  list;

    }

    @Override
    public MyArea getAreaByID(Integer areaid)
    {
        List<MyArea> list=baseMapper.getAreaByID(areaid);

        if (null == list || list.size() ==0)
        {
            return  null;
        }
        return  list.get(0);
    }
}
