package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.mapper.MyAreaMapper;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreMeterTest {

    @Autowired
    private MyMeterMapper mymapper;

    @Test
    public void testGetRoomMeters(){


        List<MyMeter> mymeterLst = mymapper.getMeterList(1);
        mymeterLst.forEach(e -> {
            System.out.println(e.toString());
        });

    }
}
