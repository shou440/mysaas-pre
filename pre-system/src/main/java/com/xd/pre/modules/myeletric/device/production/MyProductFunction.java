package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.domain.MyProductFunctionInfo;

import java.util.ArrayList;
import java.util.List;

public class MyProductFunction implements IProductFunction {

    //产品名称
    private String product_name="";

    //设备名称
    private String device_name = "";

    //功能名称
    private String fun_name = "";

    //命令流水号
    private int fun_sn=0;

    //过期时间
    private int max_seconds=0;

    //过期时间
    private int expired_tick=0;

    //过期时间
    private int upt_tick=0;

    //函数描述
    private String fun_dec = "";

    //参数列表
    private List<MyProductFunctionParam> lst_param = new ArrayList<MyProductFunctionParam>();

    @Override
    public String getProductName() {
        return product_name;
    }

    @Override
    public void setProductName(String productName) {
        product_name = productName;
    }

    @Override
    public String getDevName() {
        return device_name;
    }

    @Override
    public void SetDevName(String name) {
        device_name = name;
    }

    @Override
    public String getFunctionName() {
        return fun_name;
    }

    @Override
    public void setFunctionName(String sName) {
        fun_name = sName;
    }

    @Override
    public int getSN() {
        return fun_sn;
    }

    @Override
    public void setSN(int sn) {
        fun_sn = sn;
    }

    @Override
    public int getMaxSeconds() {
        return max_seconds;
    }

    @Override
    public void setMaxSeconds(int nSeconds) {
        max_seconds = nSeconds;
    }

    @Override
    public int getExpiredTick() {
        return expired_tick;
    }



    @Override
    public List<MyProductFunctionParam> getParams() {
        return lst_param;
    }

    @Override
    public void addParam(MyProductFunctionParam param) {
        lst_param.add(param);
    }

    @Override
    public int getUptTick() {
        return upt_tick;
    }

    @Override
    public void setUptTick(int nTick) {
        upt_tick = nTick;
    }

    public MyProductFunction()
    {

    }

    //参数化构造函数
    public MyProductFunction(MyProductFunctionInfo info)
    {
        if (null == info)
        {
            return;
        }

        product_name = info.getProduct_name();
        device_name = "";
        fun_name = info.getFunction_name();
        fun_sn = 0;
        max_seconds = info.getMax_seconds();
        upt_tick = info.getUpt_tick();
        fun_dec = info.getFunction_dec();
        upt_tick = info.getUpt_tick();
        expired_tick = 0;
    }
}
