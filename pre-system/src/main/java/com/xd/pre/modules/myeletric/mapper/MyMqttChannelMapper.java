package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.device.channel.MyMqttChannel;
import com.xd.pre.modules.myeletric.domain.MyChannelInfo;
import com.xd.pre.modules.myeletric.domain.MyChannelMqttInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyMqttChannelMapper extends BaseMapper<MyChannelMqttInfo> {

    public List<MyChannelMqttInfo> getChannel(@Param("channel_id") Integer channel_id);

}
