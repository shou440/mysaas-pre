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

    public Integer addNewDevice(@Param("device") MyProductDeviceInfo device);

    public Integer getMaxDeviceNO();

    public Integer getSubDeviceNOByIMEI(@Param("imei") String imei);

    public List<MyProductDeviceInfo> getSubDeviceByGroup(@Param("device_group") String device_group);


}
