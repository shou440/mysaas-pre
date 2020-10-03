package com.xd.pre.modules.pay;


import com.xd.pre.modules.myeletric.device.command.CommandContainer;
import com.xd.pre.modules.myeletric.device.command.IMyCommand;
import com.xd.pre.modules.myeletric.device.command.MyChargeCommand;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.dto.MeterChargeDto;
import com.xd.pre.modules.myeletric.dto.MeterCommandDto;

//微信网络充值电费
public class WXChargePayment extends WXPayment {

    protected MeterChargeDto meter_charge_dto = null;
    protected MyMeter        charge_meter = null;



    public WXChargePayment(PaymentInfo pay,MeterChargeDto meterChargeDto,MyMeter meter) {
        super(pay);
        meter_charge_dto = meterChargeDto;
        charge_meter = meter;
    }

    //支付成功后，创建充值命令
    @Override
    protected void state_cmplt()
    {
        MeterCommandDto commandDto = new MeterCommandDto();
        commandDto.setCommand_id(IMyCommand.COMMAND_CHARGE);
        commandDto.setCommand_sn(meter_charge_dto.getCommand_sn());
        commandDto.setMeter_id(meter_charge_dto.getMeter_id());



        if (null == charge_meter || null == meter_charge_dto)
        {

            error_message="创建充值命令失败!";
            SwitchState(IPayment.PAY_STATE_COMMIT_ABORT);
            return;
        }

        if (charge_meter.getEp_price() <= 0)
        {
            error_message="电表电价配置错误导致支付异常!";
            SwitchState(IPayment.PAY_STATE_COMMIT_ABORT);
            return;
        }

        //支付成功后，创建充值命令
        MyChargeCommand chargecommand = (MyChargeCommand)CommandContainer.getInstance().CreateCommand(commandDto);

        if (null == chargecommand)
        {
            error_message="创建充值命令失败!";
            SwitchState(IPayment.PAY_STATE_COMMIT_ABORT);
            return;
        }

        //设置充值金额和度数，然后将充值命令加入系统缓冲队列，开始充值
        float fTotalEP = meter_charge_dto.getCharge_fee()/charge_meter.getEp_price();
        chargecommand.SetChargeFee(meter_charge_dto.getCharge_fee());
        chargecommand.SetChargeEP(fTotalEP);

        //将充值命令加入系统缓冲队列
        CommandContainer.getInstance().AddNewCommand(chargecommand);
        
        SwitchState(IPayment.PAY_STATE_PROMOTION_PREPARE);

    }

}
