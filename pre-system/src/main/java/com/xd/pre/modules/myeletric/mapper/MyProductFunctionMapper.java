package com.xd.pre.modules.myeletric.mapper;

import com.xd.pre.modules.myeletric.domain.MyProductFunctionInfo;
import com.xd.pre.modules.myeletric.domain.MyProductPropertyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyProductFunctionMapper {

    public List<MyProductFunctionInfo> getProductFunction(@Param("product_name") String product_name);

}
