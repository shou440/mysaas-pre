package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository

public interface MyUserMeterMapper extends BaseMapper<MyUserMeter> {

    //获取用户电表映射表
    public List<MyUserMeter> getUserMeterByMeterid(@Param("meterid") Integer meterid);

}
