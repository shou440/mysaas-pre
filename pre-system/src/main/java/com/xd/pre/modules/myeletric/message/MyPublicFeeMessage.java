package com.xd.pre.modules.myeletric.message;

import com.xd.MyWeixinStub;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import org.joda.time.DateTime;
import org.weixin4j.component.MessageComponent;
import org.weixin4j.model.message.template.TemplateData;

public class MyPublicFeeMessage extends  MyMessage {

    private String open_id;
    private String msg_title;
    private String fee_sn;
    private String addr_roomname;
    private String fee_total;
    private String tm_str;

    public MyPublicFeeMessage(String openid,
                              String title,
                              String feeSN,
                              String sAddrRoomName,
                              String sFee,
                              String tmStr
                              )
    {
        open_id = openid;
        msg_title = title;
        fee_sn = feeSN;
        addr_roomname = sAddrRoomName;
        fee_total = sFee;
        tm_str = tmStr;
    }

    @Override
    public void ProcessMessage() {

        String title = "收电费通知";

        //通过微信发送给租户
        MyWeixinStub.getTheMyWeixinStub().SendFeeTemplateMessage(open_id,title,fee_sn,addr_roomname,fee_total,tm_str);


    }
}
