package com.xd.pre.modules.myeletric.device.command;

import com.xd.pre.common.utils.CommonFun;
import com.xd.pre.modules.myeletric.device.production.IDevice;
import com.xd.pre.modules.myeletric.device.production.IProductProperty;
import com.xd.pre.modules.myeletric.device.production.ProductionContainer;
import com.xd.pre.modules.myeletric.domain.MyCommandInfo;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.mydb.MyDbStub;
import com.xd.pre.modules.myeletric.vo.MyMeterVo;

public class MyMeterCommand extends MyCommand {

    public MyMeterCommand(MyCommandInfo commandInfo) {
        super(commandInfo);
    }


}
