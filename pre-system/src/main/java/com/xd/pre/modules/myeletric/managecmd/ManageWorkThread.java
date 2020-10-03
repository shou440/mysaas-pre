package com.xd.pre.modules.myeletric.managecmd;

import com.xd.pre.modules.myeletric.device.channel.IMyChannel;

public class ManageWorkThread  extends  Thread{



    public ManageWorkThread(){

    }



    @Override
    public void run() {

        ManageStub.getSinTon().Work();
    }

}
