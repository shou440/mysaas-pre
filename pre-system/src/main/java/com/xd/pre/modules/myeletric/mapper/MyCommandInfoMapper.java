package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;
import com.xd.pre.modules.myeletric.dto.MyCommandInfoDto;
import com.xd.pre.modules.myeletric.dto.MyMeterBasePriceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository

public interface MyCommandInfoMapper extends BaseMapper<MyCommandInfo> {

    public List<MyCommandInfo> getCommandByUserID(@Param("user_id") Integer user_id);
    public List<MyCommandInfo> getCommandByDevice(@Param("device_name") String device_name);
    public Integer updateCommandResult(@Param("commandInfo") MyCommandInfo commandInfo);
    public Integer saveCommandInfo(@Param("commandInfo") MyCommandInfo commandInfo);
    public Integer getMaxCommandSN();
}
