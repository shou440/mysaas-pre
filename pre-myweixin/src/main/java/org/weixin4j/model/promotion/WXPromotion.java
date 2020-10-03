package org.weixin4j.model.promotion;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一下单
 *
 * 接口文档：https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
 *
 * <b>应用场景</b>
 *
 *  微信付款零钱到个人
 *
 * <b>是否需要证书</b>
 *
 * 不需要
 *
 * @author yangqisheng
 * @since 0.0.4
 */

public class WXPromotion {

    private String mch_appid;            //公众账号ID
    private String mchid;                //商户号
    private String nonce_str;           //随机字符串
    private String partner_trade_no;    //商户订单号
    private String openid;              //用户openid
    private String amount;              //金额
    private String desc;                 //描述
    private String spbill_create_ip;    //IP地址
    private String sign;                //签名



    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("mch_appid", mch_appid);
        map.put("mchid", mchid);
        map.put("nonce_str", nonce_str);
        map.put("partner_trade_no", partner_trade_no);
        map.put("openid", openid);
        map.put("check_name", "NO_CHECK");
        map.put("amount", amount);
        map.put("desc", desc);
        map.put("spbill_create_ip", spbill_create_ip);


        return map;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        sb.append("<mch_appid><![CDATA[").append(mch_appid).append("]]></mch_appid>");
        sb.append("<mchid><![CDATA[").append(mchid).append("]]></mchid>");
        sb.append("<nonce_str><![CDATA[").append(nonce_str).append("]]></nonce_str>");
        sb.append("<partner_trade_no><![CDATA[").append(partner_trade_no).append("]]></partner_trade_no>");
        sb.append("<openid><![CDATA[").append(openid).append("]]></openid>");
        sb.append("<check_name><![CDATA[").append("NO_CHECK").append("]]></check_name>");
        sb.append("<amount><![CDATA[").append(amount).append("]]></amount>");
        sb.append("<desc><![CDATA[").append(desc).append("]]></desc>");
        sb.append("<spbill_create_ip><![CDATA[").append(spbill_create_ip).append("]]></spbill_create_ip>");
        sb.append("<sign><![CDATA[").append(sign).append("]]></sign>");
        sb.append("</xml>");
        return sb.toString();
    }

    public void setMch_appid(String appid) {
        this.mch_appid = appid;
    }

    public void setMchid(String mch_id) {
        this.mchid = mch_id;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.partner_trade_no = out_trade_no;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setAmount(String sAmount) {this.amount = sAmount;}

    public void setDec(String dec){this.desc = dec;}

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


}
