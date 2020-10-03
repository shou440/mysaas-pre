package com.xd.pre.modules.myeletric.buffer;


//数据缓冲对象接口
public interface IRedisBufferItem {

     String key();
     void setKey(String key);

     void setValue(Object obj);
     Object getValue();

     int getExipreSeconds();
     boolean isPeristen();
}
