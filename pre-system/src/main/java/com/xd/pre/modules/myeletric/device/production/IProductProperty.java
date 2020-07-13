package com.xd.pre.modules.myeletric.device.production;

public interface IProductProperty {

    //属性值当前是否新鲜有效
    boolean IsPropertyValid();

    //产品名称
    String getProductName();
    void setProductName(String productName);

    //设备名称:只有设备才用到
    String getDevName();
    void SetDevName(String name);

    // 属性名称
    String getPropertyName();
    void setPropertyName(String sName);

    //属性描述
    String getPropertyDec();
    void setPropertyDec(String dec);

    //值的类型
    int getValueType();
    void setValueType(int nType);

    //小数位
    int getFloatBits();
    void setFloatBits(int bits);

    //值
    int getValue();
    void setValue(int nValue);
    void setFloatValue(float fValue);
    void setStringValue(String sValue);

    //获取浮点数值
    float getFloatValue();


    //单位
    String getWeight();
    void setWeight(String weight);

    //更新时间
    int getFreshTick();
    void setFreshTick(int nTick);

    //修改时间
    int getUptTick();
    void setUptTick(int nTick);



    IProductProperty copy(String sDeviceName);

}
