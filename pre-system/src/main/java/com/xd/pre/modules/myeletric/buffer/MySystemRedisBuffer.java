package com.xd.pre.modules.myeletric.buffer;


import com.xd.pre.modules.myeletric.device.gather.IDeviceGather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//系统的Redis数据缓冲单件对象
public class MySystemRedisBuffer {


    //写缓冲数据队列
    private LinkedBlockingQueue<IRedisBufferItem> save_queue = new LinkedBlockingQueue<IRedisBufferItem>();

    //单件对象
    public static MySystemRedisBuffer sinTon = null;

    //Redis系统句柄
    @Autowired
    private StringRedisTemplate redisTemplate;

    //获取单件对象
    public static MySystemRedisBuffer getTheSinTon()
    {
        if (null == sinTon)
        {
            sinTon = new MySystemRedisBuffer();
        }

        return sinTon;
    }

    //设置Redis句柄
    public  void setRedis(StringRedisTemplate redis)
    {
        redisTemplate = redis;
    }

    //读取Redis数据
    public String getReisItemString(String sKey)
    {
        String sValue = "";

        try {
            sValue = (String)redisTemplate.opsForValue().get(sKey);

        }
        catch (Exception ex)
        {

        }

        return sValue;
    }

    //保存数据
    public void SaveBufferItem(String key,Object value)
    {
         RedisStringBuffer item = new RedisStringBuffer(key,value);
         try
         {
             save_queue.put(item);
         }
         catch (Exception ex)
         {
             System.err.println("Redis缓存异常: " + ex.getMessage());
         }
    }

    //保存数据
    public void SaveBufferItem(String key,Object value,boolean persisten)
    {
        RedisStringBuffer item = new RedisStringBuffer(key,value,persisten);
        try
        {

            save_queue.put(item);
        }
        catch (Exception ex)
        {
        }
    }

    //保存回调函数
    public void CallTick()
    {
        if (save_queue.size() == 0 || redisTemplate == null)
        {
            return;
        }

        try
        {
            while(save_queue.size() != 0)
            {

                IRedisBufferItem buffer = (IRedisBufferItem)save_queue.poll();
                if (null != buffer)
                {
                    String key = buffer.key();
                    String sValue = (String)buffer.getValue();

                    if (buffer.isPeristen())   //长期有效
                    {
                        redisTemplate.opsForValue().setIfPresent(key,sValue);  //设置数据长期有效
                    }
                    else
                    {
                        redisTemplate.opsForValue().set(key,sValue,buffer.getExipreSeconds(), TimeUnit.SECONDS);  //设置数据有效期为1个月

                    }

                }
            }

        }
        catch (Exception ex)
        {
            System.err.println("保存实时数据异常: " + ex.getMessage());
        }

    }

}
