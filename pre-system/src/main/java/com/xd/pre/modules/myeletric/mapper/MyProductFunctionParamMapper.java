package com.xd.pre.modules.myeletric.mapper;

import com.xd.pre.modules.myeletric.domain.MyProductFunctionParamInfo;
import com.xd.pre.modules.myeletric.dto.MyProductFunctionParamQry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyProductFunctionParamMapper {

    public List<MyProductFunctionParamInfo> getProductFunctionParam(@Param("queryParam") MyProductFunctionParamQry queryParam);

}
