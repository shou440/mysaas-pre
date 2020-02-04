package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface MyRoomMapper extends BaseMapper<MyRoom>{

   // List<MyRoom> getRoomInfo(MyRoomDto myroomdto);

  public List<MyRoom> getRoomInfo();

}
