package com.xd;

import org.weixin4j.Weixin;
import org.weixin4j.loader.ITokenLoader;
import org.weixin4j.model.base.Token;

public class MyWeixin extends Weixin {

    //Token对象
    private Token m_token = null;

    public MyWeixin()
    {
        super();
    }

    public  void SetToken(Token token)
    {
        this.tokenLoader.refresh(token);
        m_token = token;
    }

    public Token freshToken()
    {
        if(null == m_token || m_token.isExprexpired())
        {
            try
            {
                m_token =  super.base().token();
                m_token.setExpires_in(6900);
                SetToken(m_token);

            }
            catch (Exception ex)
            {
                System.out.print("获取Token异常:"+ex.getMessage());
            }

        }

        System.out.print("获取Token:"+m_token);

        return m_token;
    }

}
