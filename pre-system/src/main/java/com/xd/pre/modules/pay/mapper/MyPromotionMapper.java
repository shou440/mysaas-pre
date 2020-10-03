package com.xd.pre.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xd.pre.modules.pay.PaymentInfo;
import com.xd.pre.modules.pay.vo.MyPromotionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyPromotionMapper extends BaseMapper<MyPromotionInfo> {

    //获取业主的结算单
    public List<MyPromotionInfo> getPromotionByUserID(@Param("user_id") Integer user_id);


}
