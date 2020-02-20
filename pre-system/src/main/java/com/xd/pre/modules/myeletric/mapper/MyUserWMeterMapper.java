package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import com.xd.pre.modules.myeletric.domain.MyUserWMeter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyUserWMeterMapper  extends BaseMapper<MyUserWMeter> {

    //获取用户水表映射表
    public List<MyUserWMeter> getWUserMeterByMeterid(@Param("meterid") Integer meterid);
}
