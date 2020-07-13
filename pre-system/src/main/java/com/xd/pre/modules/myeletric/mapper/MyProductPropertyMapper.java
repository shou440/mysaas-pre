package com.xd.pre.modules.myeletric.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyProductInfo;
import com.xd.pre.modules.myeletric.domain.MyProductPropertyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyProductPropertyMapper  extends BaseMapper<MyProductPropertyInfo> {

    public List<MyProductPropertyInfo> getProductProperty(@Param("product_name") String product_name);
}
