package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyTalentInfo;



public interface IMyTalentInfoService extends IService<MyTalentInfo> {

    MyTalentInfo getTanlentInfo(Integer roomid);
}
