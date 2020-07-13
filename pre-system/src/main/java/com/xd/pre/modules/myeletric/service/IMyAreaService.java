package com.xd.pre.modules.myeletric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xd.pre.modules.myeletric.domain.MyArea;


import java.util.List;

public interface IMyAreaService extends IService<MyArea> {

    List<MyArea> getAreaByOwnerOpenid(Integer userid);

    MyArea getAreaByID(Integer areaid);


}
