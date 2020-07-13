package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyProductSignalInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository

public interface MyProductSignalMapper extends BaseMapper<MyProductSignalInfo> {

    public List<MyProductSignalInfo> getProductSignal(@Param("product_name") String product_name);
}
