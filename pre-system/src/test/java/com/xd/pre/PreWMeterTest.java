package com.xd.pre;


import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyUserWMeter;
import com.xd.pre.modules.myeletric.domain.MyWMeter;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.myeletric.mapper.MyUserWMeterMapper;
import com.xd.pre.modules.myeletric.service.IMyUserWMeterService;
import com.xd.pre.modules.myeletric.service.IMyWMeterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreWMeterTest {

    @Autowired
    private IMyWMeterService service;

    @Autowired
    private MyUserWMeterMapper map;

    @Test
    public void testGetRoomMeters(){


        List<MyWMeter> mymeterLst = service.getMeterList(1);
        mymeterLst.forEach(e -> {
            System.out.println(e.toString());
        });

        List<MyUserWMeter> lst = map.getWUserMeterByMeterid(1);
        lst.forEach(e -> {
            System.out.println(e.toString());
        });

    }
}
