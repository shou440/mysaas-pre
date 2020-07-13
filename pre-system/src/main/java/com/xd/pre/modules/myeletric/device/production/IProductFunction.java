package com.xd.pre.modules.myeletric.device.production;

import java.util.List;

public interface IProductFunction {

    //产品名称
    String getProductName();
    void setProductName(String productName);

    //设备名称:只有设备才用到
    String getDevName();
    void SetDevName(String name);

    // 函数名称
    String getFunctionName();
    void setFunctionName(String sName);

    //流水号
    int getSN();
    void setSN(int sn);

    //执行的最大秒数
    int getMaxSeconds();
    void setMaxSeconds(int nSeconds);

    //执行的最大秒数
    int getExpiredTick();

    //参数列表
    List<MyProductFunctionParam> getParams();
    void addParam(MyProductFunctionParam param);

    //修改时间
    int getUptTick();
    void setUptTick(int nTick);
}
