package com.xd.pre.modules.myeletric.mapper;

import com.xd.pre.modules.myeletric.domain.MyProductDeviceInfo;
import com.xd.pre.modules.myeletric.domain.MyProductEventInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyProductDeviceMapper {

    //获取产品的设备列表
    public List<MyProductDeviceInfo> getProductDevice(@Param("product_name") String product_name);
}
