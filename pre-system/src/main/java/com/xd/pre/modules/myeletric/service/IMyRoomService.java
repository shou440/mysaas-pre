package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyRoom;


import java.util.List;

public interface IMyRoomService extends IService<MyRoom>{

     List<MyRoom> getRoomInfo();


}
