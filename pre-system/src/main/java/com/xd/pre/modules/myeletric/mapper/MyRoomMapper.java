package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface MyRoomMapper extends BaseMapper<MyRoom>{

  //获取园区的房间列表
  public List<MyRoom> getRoomInfo(@Param("areaid") Integer areaid);

  //根据房间编号获取房间对象
  public List<MyRoom> getRoomByID(@Param("roomid") Integer roomid);

  //更新房间基本信息
  public int updateRoominfo(@Param("room") MyRoomDto myroom);

  //更新房间的租户信息
  public int updateRoomtenant(@Param("room") MyRoomDto myroom);

  //更新房间的租赁状态
  public int updateRoomStatus(@Param("room") MyRoomDto myroom);



  //注册新房间
  public int createNewRoom(@Param("room") MyRoomDto myroom);
}
