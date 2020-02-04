package com.xd;

import com.xd.pre.log.annotation.SysOperaLog;
import org.weixin4j.Weixin;
import org.weixin4j.WeixinBuilder;
import org.weixin4j.model.base.Token;

/**
 * @Classname MyWeixinStub
 * @Description 微信功能包装类
 * @Author 庄学荣
 * @Date 2020-02-04
 * @Version 0.0.1
 */
public class MyWeixinStub {

    //单件对象
    private static  MyWeixinStub sinTon = null;

    //微信对象
    private Weixin weixin = null;

    //单件对象接口
    public  static  MyWeixinStub getTheMyWeixinStub()
    {
        if (null == sinTon)
        {
            sinTon = new MyWeixinStub();
        }

        return sinTon;
    }

    //启动微信功能
    @SysOperaLog(descrption = "启动微信")
    public  void StartWeixin()
    {
        WeixinBuilder wxbuilder = WeixinBuilder.newInstance("wx5c451c1c46ba0ff9","41f76af6ab2478dccbcdd4d741cb5d02");
        weixin = wxbuilder.build();

        try
        {
            Token token = weixin.getToken();
            String sToken = token.getAccess_token();
        }
        catch (Exception ex)
        {

        }

    }

}
