package com.xd.pre.modules.myeletric.mapper;


import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyRoomTenant;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import com.xd.pre.modules.myeletric.dto.MyRoomTenantDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyRoomTenantMapper {


    //获取所有房间的出租记录
    public List<MyRoomTenant> getAllTenant();

    //获取房间出租信息
    public List<MyRoomTenant> getRoomLessor(@Param("roomid") Integer roomid);

    //注册新房间租赁管理
    public int createNew(@Param("roomid") Integer roomid);

    //出租房间
    public int LessorRoom(@Param("roomtenant") MyRoomTenantDto roomtenant);

    //清空当前的租赁记录
    public int ResetTenant(@Param("roomid") Integer roomid);

    //更新房间的租户信息
    public void updateRoomtenant(@Param("room") MyRoomDto myroom);

}
