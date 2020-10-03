package com.xd.pre.modules.myeletric.buffer;

public class RedisStringBuffer  implements IRedisBufferItem{

    private String item_key = "";
    private Object item_obj = null;
    private boolean is_persisten = false;    //长期有效
    private int   time_expire =864000; //10天的秒数

    public RedisStringBuffer(String key,Object obj)
    {
        item_key = key;
        item_obj = obj;
    }

    //设置长期有效
    public RedisStringBuffer(String key,Object obj,boolean isPersisten)
    {
        item_key = key;
        item_obj = obj;
        is_persisten = isPersisten;
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

    @Override
    public int getExipreSeconds() {
        return time_expire;
    }

    @Override
    public boolean isPeristen() {
        return is_persisten;
    }
}
