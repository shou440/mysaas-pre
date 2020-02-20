package com.xd.pre.modules.sys.util;

import com.alibaba.fastjson.JSON;
import com.xd.pre.modules.sys.vo.RedisVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Classname RedisUtil
 * @Description redis工具类
 * @Author Created by Lihaodong (alias:小东啊) lihaodongmail@163.com
 * @Date 2019-07-22 16:15
 * @Version 1.0
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 获取所有key value
     *
     * @return
     */
    public List<RedisVo> getAll() {



        List<RedisVo> redisList = new ArrayList<>();
        // 获取所有key
        Set<Object> keys = redisTemplate.keys("*");
        if (keys != null) {
            redisList = keys.stream().map(i -> new RedisVo((String) i, JSON.toJSONString(redisTemplate.opsForValue().get(i)), redisTemplate.getExpire(i))).collect(Collectors.toList());
        }
        return redisList;
    }

    /**
     * 通过key删除value
     *
     * @param keys
     * @return
     */
    public boolean removeKey(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    public Object get(String key){
        redisTemplate.setEnableTransactionSupport(true);
        return key==null?null:redisTemplate.opsForValue().get(key);
    }


    public DataType keys(String key) {
        try {
            return redisTemplate.type(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过key和item判断是否有集合存在item项目
     *
     * @param key：健值
     *  @param item:子健
     * @return
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }



    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
}
