package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import org.springframework.stereotype.Service;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;







import java.util.List;

@Service
public class MyRoomServiceImpl extends ServiceImpl<MyRoomMapper,MyRoom> implements IMyRoomService {



    @Override
   public List<MyRoom> getRoomInfo(){
      // System.out.print(myroomdto);
      List<MyRoom> list=baseMapper.getRoomInfo();
         list.forEach(e->{
             System.out.print(e.toString());
         });
        return  list;

    }
}
