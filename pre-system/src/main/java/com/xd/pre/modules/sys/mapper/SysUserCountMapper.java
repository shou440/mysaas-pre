package com.xd.pre.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.sys.domain.SysUserCount;
import com.xd.pre.modules.sys.dto.SysUserCountDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserCountMapper extends BaseMapper<SysUserCount> {

    public List<SysUserCount> getUserCountByUserID(@Param("user_id") Integer user_id);

    public List<SysUserCount> getUserByCount(@Param("user_count") String user_count);

    //注册新业主账号
    public int createUserCount(@Param("usercount") SysUserCountDTO usercount);

    //更新业主账号
    public int updateUserCount(@Param("usercount") SysUserCountDTO usercount);
}
