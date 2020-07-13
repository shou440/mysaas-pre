package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyProductEventInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


//
@Mapper
@Repository
public interface MyProductEventMapper extends BaseMapper<MyProductEventInfo> {

    //获取设备事件列表
    public List<MyProductEventInfo> getProductEvent(@Param("product_name") String product_name);
}
