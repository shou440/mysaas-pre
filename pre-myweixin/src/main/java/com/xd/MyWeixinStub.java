package com.xd;

import org.weixin4j.Weixin;
import org.weixin4j.WeixinBuilder;
import org.weixin4j.WeixinConfig;
import org.weixin4j.WeixinPayConfig;
import org.weixin4j.model.base.Token;
import org.weixin4j.model.pay.UnifiedOrder;
import org.weixin4j.model.pay.UnifiedOrderResult;
import org.weixin4j.model.sns.SnsUser;
import org.weixin4j.util.SignUtil;

public class MyWeixinStub {

    //单件对象
    private static MyWeixinStub sinTon = null;

    //微信对象
    private Weixin weixin = null;

    //单件对象接口
    public static MyWeixinStub getTheMyWeixinStub() {
        if (null == sinTon) {

            WeixinPayConfig wxpaycon = new WeixinPayConfig();
            wxpaycon.setPartnerId("1495396332");                          //微信商户号
            wxpaycon.setPartnerKey("41f76af6ab2478dccbcdd4d743332361");   //设置商户号的Key
            wxpaycon.setCertPath("apiclient_cert.p12");                   //设置商户证书位置
            wxpaycon.setCertSecret("1495396332");                         //设置证书密码
            wxpaycon.setNotifyUrl("http://www.mbcharge.com/weixinpay");   //微信支付回调地址

            WeixinConfig wxcon = new WeixinConfig();

            sinTon = new MyWeixinStub();
        }

        return sinTon;
    }

    //启动微信功能
    public void StartWeixin() {

        //配置微信的基本信息


        //配置微信的支付信息

        WeixinBuilder wxbuilder = WeixinBuilder.newInstance("wx5c451c1c46ba0ff9", "41f76af6ab2478dccbcdd4d741cb5d02");
        weixin = wxbuilder.build();

        try {
            Token token = weixin.getToken();
            String sToken = token.getAccess_token();
        } catch (Exception ex) {

        }
    }

    //通过Code获取微信用户信息
    public SnsUser GetUserInfo(String code) {

        try {
             SnsUser user = weixin.sns().getSnsUserByCode(code);
             return  user;
        } catch (Exception ex) {

            return  null;

        }
    }

    //获取统一下单订单信息,用于客户支付
    public UnifiedOrderResult getUnifiedOrder(String openid,String paymentID,String message,float fFee) {

        try {
            //统一下单对象
            UnifiedOrder unifiedorder = new UnifiedOrder();
            unifiedorder.setAppid(weixin.getAppId());
            unifiedorder.setBody("投票订单支付描述内容");
            unifiedorder.setMch_id(weixin.getWeixinPayConfig().getPartnerId());
            unifiedorder.setNonce_str(java.util.UUID.randomUUID().toString().substring(0, 15));
            unifiedorder.setNotify_url(weixin.getWeixinPayConfig().getNotifyUrl());
            unifiedorder.setOpenid(openid);
            unifiedorder.setOut_trade_no(paymentID);
            String ip = "211.149.169.214";
            unifiedorder.setSpbill_create_ip(ip);

            //总费用
            String total_fee = (fFee * 100) + "";
            unifiedorder.setTotal_fee(total_fee.substring(0, total_fee.indexOf(".")));
            unifiedorder.setTrade_type("JSAPI");

            //获取商户密钥
            String partnerKey = weixin.getWeixinPayConfig().getPartnerKey();
            //对下单对象进行签名
            String sign = SignUtil.getSign(unifiedorder.toMap(), partnerKey);
            //设置签名
            unifiedorder.setSign(sign);

            //统一预下单
            UnifiedOrderResult unifiedOrderResult = weixin.pay().payUnifiedOrder(unifiedorder);

            return  unifiedOrderResult;

        } catch (Exception ex) {

            return  null;

        }
    }
}
