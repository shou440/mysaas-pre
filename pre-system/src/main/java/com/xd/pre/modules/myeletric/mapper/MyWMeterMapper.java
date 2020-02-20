package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.myeletric.dto.MyMeterFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyWMeterMapper extends BaseMapper<MyWMeter> {

    //获取房间的所有水表
    public List<MyWMeter> getWMeterList(@Param("roomid") Integer roomid);

    //根据水表号获取水表
    public List<MyWMeter> getWMeter(@Param("meterid") Integer meterid);

    //绑定水表到指定的房间
    public Integer bindWMeter(@Param("filter") MyMeterFilter filter);

    //解绑水表
    public Integer unBindWMeter(@Param("meterid") Integer meterid);
}
