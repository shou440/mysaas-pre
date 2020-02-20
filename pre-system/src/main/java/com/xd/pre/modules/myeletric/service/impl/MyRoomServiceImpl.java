package com.xd.pre.modules.myeletric.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import org.springframework.stereotype.Service;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;







import java.util.List;

@Service
public class MyRoomServiceImpl extends ServiceImpl<MyRoomMapper,MyRoom> implements IMyRoomService {



    @Override
   public List<MyRoom> getRoomInfo(Integer areaid){

      List<MyRoom> list=baseMapper.getRoomInfo(areaid);
         list.forEach(e->{
             System.out.print(e.toString());
         });
        return  list;

    }

    @Override
    public  MyRoom getRoomByID(Integer roomid){

        List<MyRoom> list=baseMapper.getRoomByID(roomid);
        if (list == null || list.size() == 0)
        {
            return null;
        }
        MyRoom room = list.get(0);
        return  room;

    }


    @Override
    public int updateRoominfo(MyRoomDto room){

          baseMapper.updateRoominfo(room);

          return 0;
    }

    @Override
    public MyRoomDto createNewRoom(MyRoomDto room){

        baseMapper.createNewRoom(room);
        return  room;

    }

}
