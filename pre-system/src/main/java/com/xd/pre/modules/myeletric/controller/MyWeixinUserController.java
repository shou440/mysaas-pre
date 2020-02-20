package com.xd.pre.modules.myeletric.controller;


import com.alibaba.fastjson.JSON;
import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.dto.MyWXUserFilterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.weixin4j.model.sns.SnsUser;



@RestController
@RequestMapping("/weixin")
public class MyWeixinUserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //查看业主的所有园区
    @RequestMapping(value = "/getwxuserinfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfo( MyWXUserFilterDto filter) {

        //获取code
        String sCode = filter.getCode();
        String uuid = filter.getParam();

        //获取当前连接的用户ID
        try {
            SnsUser user = MyWeixinStub.getTheMyWeixinStub().GetUserInfo(sCode);
            String ret = JSON.toJSONString(user);

            //将用户信息记录到Redis缓冲中
            redisTemplate.opsForValue().set(uuid,ret);

            return R.ok(ret);
        } catch (Exception ex) {

            return R.error("获取微信用户信息失败");
        }
    }

    //通过uuid获取微信认证用户的信息
    @RequestMapping(value = "/getwxuserinfobyuuid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfobyUUID( MyWXUserFilterDto filter) {

        //获取code
        String uuid = filter.getParam();

        //获取当前连接的用户ID
        try {

            //将用户信息记录到Redis缓冲中
            String userinfo = redisTemplate.opsForValue().get(uuid);
            if (userinfo != null)
            {
                return R.ok(userinfo);
            }
            else
            {
                return R.error("获取用户微信信息为空");
            }

        } catch (Exception ex) {

            return R.error("获取微信用户信息失败");
        }
    }

}
