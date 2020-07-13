package com.xd;

import org.springframework.beans.factory.annotation.Autowired;
import org.weixin4j.*;
import org.weixin4j.component.MessageComponent;
import org.weixin4j.component.PayComponent;
import org.weixin4j.model.base.Token;
import org.weixin4j.model.message.template.TemplateData;
import org.weixin4j.model.pay.*;
import org.weixin4j.model.sns.SnsUser;
import org.weixin4j.model.user.User;
import org.weixin4j.util.PayUtil;
import org.weixin4j.util.SignUtil;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyWeixinStub {

    //单件对象
    private static MyWeixinStub sinTon = null;
    private static final byte[] LOCK = new byte[0];
    private  int kk = 0;

    //微信对象
    private MyWeixin weixin = null;



    //单件对象接口
    public static MyWeixinStub getTheMyWeixinStub() {

        synchronized(LOCK) {
            if (null == sinTon) {
                sinTon = new MyWeixinStub();
            }

            sinTon.kk++;
        }

        return sinTon;
    }

    //启动微信功能
    public void StartWeixin() {

        //配置微信的基本信息
     /*   wxpaycon = new WeixinPayConfig();
        wxpaycon.setPartnerId("1495396332");                          //微信商户号
        wxpaycon.setPartnerKey("41f76af6ab2478dccbcdd4d743332361");   //设置商户号的Key
        wxpaycon.setCertPath("apiclient_cert.p12");                   //设置商户证书位置
        wxpaycon.setCertSecret("1495396332");                         //设置证书密码
        wxpaycon.setNotifyUrl("http://www.mbcharge.com/weixinpay");   //微信支付回调地址

        //配置微信的支付信息
        wxcon = new WeixinConfig();
        wxcon.setAppid("wx5c451c1c46ba0ff9");
        wxcon.setSecret("41f76af6ab2478dccbcdd4d741cb5d02");

        WeixinBuilder wxbuilder = WeixinBuilder.newInstance(wxcon,wxpaycon);
        weixin = wxbuilder.build();*/

        //获取Token
        try {
            weixin = new MyWeixin();
            Token token = weixin.freshToken();
            token.setExpires_in(6900);
            //Configuration.isDebug()

          //  weixin.SetToken(token);


           // token = weixin.getToken();
            System.out.print("缓存后的Token:"+token);

        } catch (Exception ex) {

            ex.printStackTrace();
            System.out.print("启动微信异常"+ex.getMessage());

        }
    }

    public  void FreshToken()
    {
        weixin.freshToken();
    }

    //通过Code获取微信用户信息
    public User GetUserInfo(String code) {

        try {
            User user = weixin.sns().getSnsUserByCode(code);
            System.out.print("获取用户数据正常:"+user);
            return  user;
        } catch (Exception ex) {

            //异常时重新刷新Token
            Token token = weixin.freshToken();
            token.setExpires_in(6900);

            System.out.print("获取用户数据发生错误1:"+ex.getMessage());
            return  null;

        }
    }

    //通过Code获取微信用户信息
    public User GetUserDetailByOpenid(String openid) {

        try {

            System.out.print("Get User Info:");
            User user = weixin.user().info(openid);

            if(null == user)
            {
                System.out.print("获取微信信息为空，请确认用户是否关注公众号");
            }
            System.out.print(user);


            return  user;
        } catch (Exception ex) {
            System.out.print("Get User Exception:"+ex.getMessage());
            return  null;

        }
    }
    //获取统一下单订单信息,用于客户支付
    public WCPay getUnifiedOrder(String openid, String paymentID, String message, float fFee) {

        try {
            //统一下单对象
            UnifiedOrder unifiedorder = new UnifiedOrder();
            unifiedorder.setAppid(weixin.getAppId());
           // unifiedorder.setBody("Tenant Fee");
            unifiedorder.setBody(message);
            unifiedorder.setMch_id(weixin.getWeixinPayConfig().getPartnerId());
            unifiedorder.setNonce_str(java.util.UUID.randomUUID().toString().substring(0, 15));
            unifiedorder.setNotify_url(weixin.getWeixinPayConfig().getNotifyUrl());
            unifiedorder.setOpenid(openid);
           // unifiedorder.setOpenid("oRvGF1YWJSGup8qiY27js5BbOf70");
            unifiedorder.setOut_trade_no(paymentID);
            String ip = "134.175.52.44";
            unifiedorder.setSpbill_create_ip(ip);

            //总费用
            String total_fee = (fFee) + "";
            unifiedorder.setTotal_fee(total_fee.substring(0, total_fee.indexOf(".")));
            unifiedorder.setTrade_type("JSAPI");

            //获取商户密钥
            String partnerKey = weixin.getWeixinPayConfig().getPartnerKey();
            //对下单对象进行签名
            String sign = SignUtil.getSign(unifiedorder.toMap(), partnerKey);
            //设置签名
            unifiedorder.setSign(sign);

            System.out.print("下单前");
            System.out.print(unifiedorder.toXML());

            //统一预下单
            UnifiedOrderResult unifiedOrderResult = weixin.pay().payUnifiedOrder(unifiedorder);

            System.out.print("下单结果");
            System.out.print(unifiedOrderResult.toString());

            WCPay wcPay= PayUtil.getBrandWCPayRequest(weixin.getAppId(),unifiedOrderResult.getPrepay_id(),partnerKey);
            return  wcPay;

        } catch (Exception ex) {

            return  null;

        }
    }

    //查询订单支付情况
    public OrderQueryResult QueryPay(String paymentID)  {




        OrderQuery query = new OrderQuery();
        query.setAppid(weixin.getAppId());
        query.setMch_id(weixin.getWeixinPayConfig().getPartnerId());
        query.setOut_trade_no(paymentID);
        query.setNonce_str(java.util.UUID.randomUUID().toString().substring(0, 15));
        String partnerKey = weixin.getWeixinPayConfig().getPartnerKey();

        //签名
        String sign = SignUtil.getSign(query.toMap(), partnerKey);
        query.setSign(sign);

        //签名类型
        query.setSign_type("MD5");

        try
        {
            PayComponent payComponent = weixin.pay();
            OrderQueryResult ret = payComponent.payOrderQuery(query);

            return  ret;
        }
        catch (Exception ex)
        {
            System.out.print("查询支付异常:"+ex.getMessage());
            return null;
        }

    }

    //收费信息通知模板
    public boolean SendFeeTemplateMessage(String openid,
                                          String title,
                                          String feeSN,
                                          String sAddrRoomName,
                                          String sFee,
                                          String tmStr )
    {
        MessageComponent messageComponent  = weixin.message();

        //标题ArrayList<>
        List<TemplateData> lstTmpData = new ArrayList<TemplateData>();
        TemplateData tmpData1 = new TemplateData("first",title);
        TemplateData tmpData2 = new TemplateData("keyword1",feeSN);
        TemplateData tmpData3 = new TemplateData("keyword2",sAddrRoomName);
        TemplateData tmpData4 = new TemplateData("keyword3",sFee);
        TemplateData tmpData5 = new TemplateData("keyword4",tmStr);
        TemplateData tmpData6 = new TemplateData("remark",sAddrRoomName);
        lstTmpData.add(tmpData1);
        lstTmpData.add(tmpData2);
        lstTmpData.add(tmpData3);
        lstTmpData.add(tmpData4);
        lstTmpData.add(tmpData5);
        lstTmpData.add(tmpData6);
        if (null != messageComponent)
        {
            try
            {
                String url = "http://www.ambcharge.com/myweixin?fun=tl&param=000001";
                messageComponent.sendTemplateMessage(openid,
                        "VVEPcqRMeBQh5JWOkzMVGvTV-hfyfZ0cEQzwObbQe1o",
                        lstTmpData,
                        url);

                System.out.print("发送消息");
            }
            catch (Exception ex)
            {

            }

        }
        return true;
    }
}
