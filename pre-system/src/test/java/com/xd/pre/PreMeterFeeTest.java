package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.myeletric.service.IMyMeterFeeService;
import com.xd.pre.modules.myeletric.service.IMyRoomService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreMeterFeeTest {

    @Autowired
    private IMyMeterFeeService myMeterFeeService;

    @Test
    public void testGetMeterFee(){

        MyMeterFeeQueryDto queryParam = new MyMeterFeeQueryDto();

        queryParam.setArea_id(1);
        queryParam.setRoom_id(0);
        queryParam.setMeter_id(0);
      //  queryParam.setStart_time(new Timestamp(2020-1900,2,1,0,0,0,0));
      //  queryParam.setEnd_time(new Timestamp(System.currentTimeMillis()));
        List<MyMeterFee> lst = myMeterFeeService.getMeterFeeByArea(queryParam);
        lst.forEach(e -> {
            System.out.println(e.toString());
        });

    }

}
