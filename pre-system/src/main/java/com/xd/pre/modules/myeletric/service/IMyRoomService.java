package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyRoomTenant;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import com.xd.pre.modules.myeletric.dto.MyRoomLessorDto;
import com.xd.pre.modules.myeletric.dto.MyRoomTenantDto;
import org.apache.ibatis.annotations.Param;


import java.sql.Timestamp;
import java.util.List;

public interface IMyRoomService extends IService<MyRoom>{

     List<MyRoom> getRoomInfo(Integer areaid);
     MyRoom getRoomByID(Integer roomid);
     MyRoomDto createNewRoom(MyRoomDto room);
     int updateRoominfo(MyRoomDto room);
     int updateRoomtenant(MyRoomDto room);

     //获取房间出租信息
     MyRoomTenant getRoomLessor(Integer roomid);

     //注册新房间租赁管理
     int createNewTenant(Integer roomid);

     //出租房间
     int LessorRoom(MyRoomLessorDto roomtenant);

     //房间退租
     int UnLessorRoom(MyRoomLessorDto roomlessor,String sErr);


     //生成房间的收费单
     int CreateRoomBill(MyRoomTenant tenant,  Timestamp startTime,Timestamp endTime,String sErr);


}
