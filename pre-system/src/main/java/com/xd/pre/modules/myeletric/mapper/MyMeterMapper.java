package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyMeterMapper  extends BaseMapper<MyMeter> {

    //获取房间的所有电表
    public List<MyMeter> getMeterList(@Param("roomid") Integer roomid);

    //根据电表号获取电表
    public List<MyMeter> getMeter(@Param("meterid") Integer meterid);

    //绑定电表到指定的房间
    public Integer bindMeter(@Param("filter") MyMeterFilter filter);

    //解绑电表
    public Integer unBindMeter(@Param("meterid") Integer meterid);


}
