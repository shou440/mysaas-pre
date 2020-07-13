package com.xd.pre.modules.myeletric.device.production;

import com.xd.pre.modules.myeletric.domain.MyProductPropertyInfo;

public class MyProductProperty implements IProductProperty {

    //产品名称
    String product_name="";

    //设备名称
    String device_name = "";

    //属性名称
    String property_name = "";

    //属性描述
    String property_dec= "";

    //小数点位数
    int float_bits = 0;

    //单位
    String property_weight="";

    //值的类型
    int value_type = 0;

    //值
    int  int_value = 0;

    //字符串值
    String str_value = "";

    //数据刷新的时间戳
    int fresh_tick = 0;

    //更新时间
    int upt_tick = 0;

    //数据有效性
    private  boolean is_data_valid = false;

    public MyProductProperty(){

    }

    //带参数构造
    public MyProductProperty(MyProductPropertyInfo info)
    {
        if (null == info)
        {
            return;
        }

        product_name = info.getProduct_name();
        device_name = "";
        property_dec = info.getProperty_dec();
        property_name = info.getProperty_name();
        float_bits = info.getFloat_bits();
        property_weight = info.getValue_weight();
        value_type = info.getValue_type();
        upt_tick = info.getUpt_tick();

    }

    @Override
    public boolean IsPropertyValid() {
        return is_data_valid;
    }

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
    public String getPropertyName() {
        return property_name;
    }

    @Override
    public void setPropertyName(String sName) {
        property_name = sName;
    }

    @Override
    public String getPropertyDec() {
        return property_dec;
    }

    @Override
    public void setPropertyDec(String dec) {
        property_dec = dec;
    }

    @Override
    public int getValueType() {
        return value_type;
    }

    @Override
    public void setValueType(int nType) {
        value_type = nType;

    }

    @Override
    public int getFloatBits() {
        return float_bits;
    }

    @Override
    public void setFloatBits(int bits) {
        float_bits = bits;
    }

    @Override
    public int getValue() {
        return int_value;
    }

    @Override
    public void setValue(int nValue) {
        int_value = nValue;
        is_data_valid = true;
    }

    @Override
    public void setFloatValue(float fValue) {

        for(int i = 0; i < getFloatBits(); i++)
        {
            fValue *= 10;
        }

        setValue((int)fValue);
    }

    @Override
    public void setStringValue(String sValue) {

        float fValue = 0.0f;
        try
        {
            fValue = Float.parseFloat(sValue);
        }
        catch (Exception ex)
        {
            return;
        }

        for(int i = 0; i < float_bits; i++)
        {
            fValue *= 10;
        }

        setValue((int)fValue);
    }

    @Override
    public float getFloatValue() {

        float fValue=  0.0f;
        fValue = getValue();

        switch (float_bits)
        {
            case 1:
            {
                fValue /= 10.0f;
                break;
            }
            case 2:
            {
                fValue /= 100.0f;
                break;
            }
            case 3:
            {
                fValue /= 1000.0f;
                break;
            }
            case 4:
            {
                fValue /= 10000.0f;
                break;
            }
            case 5:
            {
                fValue /= 100000.0f;
                break;
            }
        }
        return fValue;
    }

    @Override
    public String getWeight() {
        return property_weight;
    }

    @Override
    public void setWeight(String weight) {
        property_weight = weight;
    }

    @Override
    public int getFreshTick() {
        return fresh_tick;
    }

    @Override
    public void setFreshTick(int nTick) {
        fresh_tick = nTick;
    }

    @Override
    public int getUptTick() {
        return upt_tick;
    }

    @Override
    public void setUptTick(int nTick) {
        upt_tick = nTick;
    }

    @Override
    public IProductProperty copy(String sDeviceName) {

        MyProductProperty property = new MyProductProperty();
        property.setProductName(product_name);
        property.SetDevName(sDeviceName);
        property.setPropertyName(property_name);
        property.setPropertyDec(property_dec);
        property.setFloatBits(float_bits);
        property.setWeight(property_weight);
        property.setValueType(value_type);
        property.setFreshTick(0);

        return property;
    }
}
