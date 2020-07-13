package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyAreaMapper extends BaseMapper<MyArea> {

    public List<MyArea> getAreaByOwner(@Param("userid") Integer userid);
    public List<MyArea> getAreaByID(@Param("areaid") Integer areaid);

}
