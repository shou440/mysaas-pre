package com.xd.pre.modules.myeletric.device.production;

public interface IProductSignal {

    //产品名称
    String getProductName();
    void setProductName(String productName);

    //设备名称:只有设备才用到
    String getDevName();
    void SetDevName(String name);

    // 信号名称
    String getSignalName();
    void setSignalName(String sName);

    //0值描述
    String getValue0Dec();
    void setValue0Dec(String dec);

    //1值描述
    String getValue1Dec();
    void setValue1Dec(String dec);

    //值
    int getValue();
    void setValue(int nValue);

    //信号描述
    String getDec();
    void setDec(String dec);


    //更新时间
    int getFreshTick();
    void setFreshTick(int nTick);

    //修改时间
    int getUptTick();
    void setUptTick(int nTick);

    IProductSignal copy(String sDeviceName);
}
