package org.weixin4j.component;

import org.weixin4j.Configuration;
import org.weixin4j.Weixin;
import org.weixin4j.WeixinException;
import org.weixin4j.WeixinPayConfig;
import org.weixin4j.http.HttpsClient;
import org.weixin4j.http.Response;
import org.weixin4j.model.pay.UnifiedOrderResult;
import org.weixin4j.model.promotion.PromotionResult;
import org.weixin4j.model.promotion.WXPromotion;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class PromotionComponect extends AbstractComponent {

    public PromotionComponect(Weixin weixin) {
        super(weixin);
    }

    /**
     * 付款给个人零钱
     *
     * @param promotion 付款结算对象
     * @return 下单返回结果对象
     * @throws org.weixin4j.WeixinException 微信操作异常
     */
    public PromotionResult payPromotion(WXPromotion promotion,WeixinPayConfig payConfig) throws WeixinException {
        //将统一下单对象转成XML
        String xmlPost = promotion.toXML();
        if (Configuration.isDebug()) {
            System.out.println("调试模式_付款个人零钱：" + xmlPost);
        }

        System.out.println("调试模式1_付款个人零钱：" + xmlPost+"\n");

        //创建请求对象
        HttpsClient http = new HttpsClient();

        //提交xml格式数据
        Response res = http.postXml("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", xmlPost, payConfig.getPartnerId(), payConfig.getCertPath(), payConfig.getCertSecret());

        //获取微信平台下单接口返回数据
        String xmlResult = res.asString();
        System.out.println("调试模式_付款个人零钱：" + xmlResult+"\n");
        try {
            JAXBContext context = JAXBContext.newInstance(PromotionResult.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            PromotionResult result = (PromotionResult) unmarshaller.unmarshal(new StringReader(xmlResult));
            return result;
        } catch (JAXBException ex) {
            return null;
        }
    }
}
