package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyChannelInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyChannelInfoMapper extends BaseMapper<MyChannelInfo> {

    public List<MyChannelInfo> getAllChannels();
    public List<MyChannelInfo> getChannel(@Param("channel_id") Integer channel_id);
}
