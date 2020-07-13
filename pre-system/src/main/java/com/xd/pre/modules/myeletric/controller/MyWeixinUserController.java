package com.xd.pre.modules.myeletric.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xd.MyWeixinStub;
import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.domain.MyWaterFee;
import com.xd.pre.modules.myeletric.dto.*;
import com.xd.pre.modules.myeletric.service.*;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;
import com.xd.pre.modules.pay.IPayment;
import com.xd.pre.modules.pay.PaymentContainer;
import com.xd.pre.modules.pay.PaymentInfo;
import com.xd.pre.modules.pay.WXPayment;
import com.xd.pre.modules.pay.dto.PaymentQryDto;
import com.xd.pre.modules.pay.mapper.MyPaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.weixin4j.Configuration;
import org.weixin4j.model.pay.OrderQueryResult;
import org.weixin4j.model.pay.UnifiedOrder;
import org.weixin4j.model.pay.UnifiedOrderResult;
import org.weixin4j.model.pay.WCPay;
import org.weixin4j.model.sns.SnsUser;
import org.weixin4j.model.user.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//处理微信端用户的请求

@RestController
@RequestMapping("/weixin")
public class MyWeixinUserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IMyMeterFeeService iMyMeterFeeService;

    @Autowired
    private IWaterFeeService iWaterFeeService;

    @Autowired
    private MyPaymentMapper paymentMapper;

    @Autowired
    private IMyMeterService iMyMeterService;

    @Autowired
    private IMyRoomService myRoomService;

    //查看业主的所有园区
    @RequestMapping(value = "/getwxuserinfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getUserInfo( MyWXUserFilterDto filter) {

        //获取code
        String sCode = filter.getCode();
        String uuid = filter.getParam();

        //获取当前连接的用户ID,先将code换成Access_Token
        try {
            User user = MyWeixinStub.getTheMyWeixinStub().GetUserInfo(sCode);
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

    @RequestMapping(value = "/getmetersbytenantopenid", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMeterByTenantOpenid(MyMeterFilter filter) {

        //读取所有的电表配置数据，然后从Redis中读取当前读数，然后返回给前端
        List<MyMeterVo> listOut = new ArrayList<MyMeterVo>();
        List<MyMeter> list=iMyMeterService.getMeterByTenantOpenid(filter.getTenant_openid());
        for(int i = 0; i < list.size(); i++)
        {
            MyMeter meter = list.get(i);
            if (null == meter)
            {
                continue;
            }



            MyMeterVo item = new MyMeterVo(meter);
            MyRoom room = myRoomService.getRoomByID(meter.getRoom_id());
            if (null != room)
            {
                item.setRoom_name(room.getRoom_name());
            }

            //通过Device获取实时数据
            ProductionContainer.getTheMeterDeviceContainer().FetchMeterEP(item);

            listOut.add(item);

        }

        return R.ok(listOut);
    }

    //通过uuid获取微信认证用户的信息
    @RequestMapping(value = "/crtunifiledorder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R createPayment( MyUnifiedOrderDto unifiedorderdto ) {

        //获取当前连接的用户ID
     /*   try {

            //创建支付订单
            String openid = unifiedorderdto.getOpenid();
            String paymentid = unifiedorderdto.getPaymentid();
            String message = unifiedorderdto.getMessage();
            float fFee = unifiedorderdto.getFFee();
            WCPay ret = MyWeixinStub.getTheMyWeixinStub().getUnifiedOrder(openid,paymentid,message,fFee);
            if (ret != null)
            {
                //预支付申请成功后，创建支付单的电子单据,并保存到数据库中，并添加支付单到系统缓冲
                IPayment pay = new WXPayment(openid,paymentid,message,fFee);
                if (pay.Save())
                {
                    PaymentContainer.GetThePaymentContainer().addPayment(pay);
                    return R.ok(ret);
                }


                return R.ok(ret);
            }
            else
            {
                return R.error("生成支付单失败!");
            }

        } catch (Exception ex) {

            return R.error("生成支付单失败!");
        }*/
     return R.ok("暂不支持");
    }


    //租户通过微信获取登录获取租户的电费单
    @RequestMapping(value = "/gettenantmeterfee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getmeterfeeByTenant( MyTenantFeeQueryDto queryParam ) {

        //获取当前连接的用户ID
        try {

            //创建支付订单
            if (null == queryParam)
            {
                return R.error("查询参数错误!");
            }

            List<MyMeterFee> meterFeeLst = iMyMeterFeeService.getMeterFeeByTenantOpenid(queryParam);

            return R.ok(meterFeeLst);

        } catch (Exception ex) {

            return R.error("下载租户电费失败!");
        }
    }

    //租户通过微信获取登录获取租户的水费单
    @RequestMapping(value = "/gettenantwaterfee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getwaterfeeByTenant( MyTenantFeeQueryDto queryParam ) {

        //获取当前连接的用户ID
        try {

            if (null == queryParam)
            {
                return R.error("查询参数错误!");
            }

            List<MyWaterFee> meterFeeLst = iWaterFeeService.getWaterFeeByTenantOpenid(queryParam);

            return R.ok(meterFeeLst);

        } catch (Exception ex) {

            return R.error("下载租户水费失败!");
        }
    }

    //微信服务器通知支付结果
    @PostMapping(value = "/notifypay")
    public R Notify(@RequestBody String notifyResultXmlStr) {

        System.out.print("接收到微信付款通知:"+notifyResultXmlStr);
        String resSuccessXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";


        try {

            JAXBContext context = JAXBContext.newInstance(OrderQueryResult.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            OrderQueryResult result = (OrderQueryResult) unmarshaller.unmarshal(new StringReader(notifyResultXmlStr));

            //检查支付是否成功


            //获取支付订单Paymentid
            String paymentid = result.getOut_trade_no();


            //查找订单号
            List<PaymentInfo> lst = paymentMapper.getPaymentByID(paymentid);
            if (null != lst && lst.size() > 0)
            {
                PaymentInfo payInfo = lst.get(0);
                if (null != payInfo)
                {

                }
            }


            return R.ok(resSuccessXml);
        } catch (JAXBException ex) {
            return R.ok("Failed");
        }


    }
        //微信端获取费单的预支付单号
    @PostMapping(value = "/crtfeeprepay")
     public R createFeePrepay(@RequestBody String feePrepayStr) {

        //获取当前连接的用户ID
        try {

            System.err.println("创建支付单: " + feePrepayStr);

            //从Json字符串中提取费单流水号,并获取相应的水费单和电费单
            JSONObject feePrepayJson = JSON.parseObject(feePrepayStr);
            if (null == feePrepayJson)
            {
                return R.error("费单数据错误!");
            }

            String openid = feePrepayJson.getString("openid");
            JSONArray jArr = feePrepayJson.getJSONArray("fee_list");
            int nUserID = 0;
            String sUerID = feePrepayJson.getString("user_id");
            nUserID = Integer.parseInt(sUerID);
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            long nTick = stamp.getTime();



            //判断费单状态是否处于等待支付状态
            List<MyMeterFee> meterFeeLst = new ArrayList<MyMeterFee>();
            List<MyWaterFee> waterFeeLst = new ArrayList<MyWaterFee>();
            boolean isFeeInvalid = true;
            float fTotalFee = 0;
            for(int i = 0; i < jArr.size(); i++)
            {
                JSONObject jFee = jArr.getJSONObject(i);
                String  fee_sn = jFee.getString("fee_sn");          //流水号
                String  fee_type = jFee.getString("fee_type");      //费单类型

                //检查费单状态是否处于等待付款中
                if (fee_sn.contains("E"))     //电费单
                {
                   MyMeterFee meterFee =   iMyMeterFeeService.getMeterFeeBySn(fee_sn);
                   if (null != meterFee)
                   {
                        if (meterFee.getFee_status() == 0)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费单未审核!");
                        }
                        else if (meterFee.getFee_status() == 2)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费单已付款!");
                        }
                        else if (meterFee.getFee_status() == 3)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费单已取消!");
                        }
                        else if (meterFee.getFee_status() == 4)
                        {
                            return R.error(meterFee.getFee_sn()+"号电费支付锁定中!");
                        }
                        else if (meterFee.getFee_status() == 1) {
                            meterFeeLst.add(meterFee);
                            fTotalFee += meterFee.getTotal_fee();
                        }
                        else
                        {
                            return R.error(meterFee.getFee_sn()+"号电费状态错误!");
                        }


                   }
                }
                else if (fee_sn.contains("W"))  //水费单
                {
                    MyWaterFee waterFee =   iWaterFeeService.getWaterFeeBySn(fee_sn);
                    if (null != waterFee)
                    {
                        if (waterFee.getFee_status() == 0)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费单未审核!");
                        }
                        else if (waterFee.getFee_status() == 2)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费单已付款!");
                        }
                        else if (waterFee.getFee_status() == 3)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费单已取消!");
                        }
                        else if (waterFee.getFee_status() == 4)
                        {
                            return R.error(waterFee.getFee_sn()+"号水费支付锁定中!");
                        }
                        else if (waterFee.getFee_status() == 1) {
                            waterFeeLst.add(waterFee);
                            fTotalFee += waterFee.getTotal_fee();
                        }
                        else
                        {
                            return R.error(waterFee.getFee_sn()+"号电费状态错误!");
                        }
                    }

                }

            }

            //创建预支付单
            WCPay pay = PaymentContainer.GetThePaymentContainer().CreateFeePayment(meterFeeLst,
                                                                                   waterFeeLst,
                                                                                   openid,
                                                                                   "水电费单",
                                                                                    fTotalFee,
                                                                                    nUserID);
            if (null == pay)
            {
                return  R.error("创建支付单错误!");
            }



            return R.ok(pay);


        } catch (Exception ex) {

            return R.error("生成支付单失败!");
        }
    }
    //查询支付单结果
    @RequestMapping(value = "/querypament", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R QueryPament( PaymentQryDto qryDto ) {

        try {

            System.out.print("查询账单1");

            if (null == qryDto || qryDto.getPayment_id() == null)
            {
                return R.error("查询账单失败!");
            }

            //如果优先指定了pamentid，则只需要查询指定账单就可以
            if (!qryDto.getPayment_id().equals(""))
            {
                 System.out.print("查询支付结果");
                  List<PaymentInfo> lst =   paymentMapper.getPaymentByID(qryDto.getPayment_id());

                  if (null != lst && lst.size() != 0)
                  {
                      PaymentInfo payInfo = lst.get(0);
                      return R.ok(payInfo);
                  }
            }

            //租户查询自己所有的记录
            if (!qryDto.getTenant_openid().equals("") && qryDto.getUser_id() == 0)
            {
                System.out.print("查询租户的支付单");
                List<PaymentInfo> lstRet = paymentMapper.getPaymentByTenant(qryDto.getTenant_openid());
                return R.ok(lstRet);
            }

            //其他需要指定时间段
            if (qryDto.getStart_time() == null || qryDto.getEnd_time() == null)
            {
                return R.error("未指定查询时间段!");
            }



            //如果只指定了UserID，则查询UserID
            if (qryDto.getUser_id() != 0)
            {
                if (qryDto.getTenant_openid().equals(""))    //租户不指定，则查询所有的
                {

                }
            }
            else if (!qryDto.getTenant_openid().equals(""))
            {

            }
            else
            {
                return R.error("账单查询条件错误!");
            }

            return R.error("账单查询失败");
        } catch (Exception ex) {
            return R.ok("账单查询异常:"+ex.getMessage());
        }
    }

}
