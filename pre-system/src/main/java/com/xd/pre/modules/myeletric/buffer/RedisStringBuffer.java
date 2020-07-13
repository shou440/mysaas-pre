package com.xd.pre.modules.myeletric.buffer;

public class RedisStringBuffer  implements IRedisBufferItem{

    private String item_key = "";
    private Object item_obj = null;

    public RedisStringBuffer(String key,Object obj)
    {
        item_key = key;
        item_obj = obj;
    }


    @Override
    public String key() {
        return item_key;
    }

    @Override
    public void setKey(String key) {
        item_key = key;
    }

    @Override
    public void setValue(Object obj) {
        item_obj = obj;
    }

    @Override
    public Object getValue() {
        return item_obj;
    }
}
