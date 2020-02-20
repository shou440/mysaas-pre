package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyTalentInfo;
import com.xd.pre.modules.myeletric.domain.MyUserMeter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyTalentInfoMapper extends BaseMapper<MyTalentInfo> {

    //获取租户信息
    public List<MyTalentInfo> getTanlentInfo(@Param("roomid") Integer roomid);
}
