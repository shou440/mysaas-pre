package com.xd.pre.modules.myeletric.mapper;


import com.xd.pre.modules.myeletric.domain.MyPropertyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MyProductPropertyRecordMapper {

    //记录属性历史数据
    public Integer recordProperty(@Param("record") MyPropertyRecord record);
}
