package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterTenantTb;
import com.xd.pre.modules.myeletric.domain.MyRoomTenant;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyRoomTenantMapper;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreRoomFeeTest {

    @Autowired
    private IMyRoomService myRoomService;

    @Autowired
    private MyRoomTenantMapper myRoomTenantMapper;

    @Test
    public void testRoomFeeService(){


        List<MyRoomTenant>  lst= myRoomTenantMapper.getRoomLessor(1);
        MyRoomTenant tenant = lst.get(0);

        Timestamp tmStart = new Timestamp(2020-1900,3-1,1,23,59,59,999);
        Timestamp tmEnd = new Timestamp(2020-1900,3-1,30,23,59,59,999);

        String sErr = "";
        myRoomService.CreateRoomBill(tenant,tmStart,tmEnd,sErr);

    }
}
