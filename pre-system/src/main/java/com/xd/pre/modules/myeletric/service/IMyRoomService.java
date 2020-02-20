package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;


import java.util.List;

public interface IMyRoomService extends IService<MyRoom>{

     List<MyRoom> getRoomInfo(Integer areaid);
     MyRoom getRoomByID(Integer roomid);
     int updateRoominfo(MyRoomDto room);
     MyRoomDto createNewRoom(MyRoomDto room);

}
