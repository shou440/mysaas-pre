package com.xd.pre.modules.myeletric.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyTenantHis;
import com.xd.pre.modules.myeletric.dto.MyTenantHisDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyTenantHisMapper extends BaseMapper<MyTenantHisMapper>  {

    //获取租赁历史记录
    public List<MyTenantHis> getTenantHisByAreaID(@Param("areaid") Integer areaid);

    public int updateTenantHis(@Param("tenantHis") MyTenantHisDto tenantHis);

     //保存租赁历史记录
     public int saveTenantHis(@Param("tenantHis") MyTenantHisDto tenantHis);
}
