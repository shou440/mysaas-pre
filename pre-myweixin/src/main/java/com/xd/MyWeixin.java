package com.xd;

import org.weixin4j.Weixin;
import org.weixin4j.loader.ITokenLoader;
import org.weixin4j.model.base.Token;

public class MyWeixin extends Weixin {


    public MyWeixin()
    {
        super();
    }



    //强制刷新基础Token
    public Token freshToken()
    {
        Token token = null;
        try
        {
            token =  super.base().token();
            this.tokenLoader.refresh(token);


        }
        catch (Exception ex)
        {
            System.out.print("刷新基础Token异常:"+ex.getMessage());
            return null;
        }

        System.out.print("刷新基础Token:"+token);
        return token;
    }

}
