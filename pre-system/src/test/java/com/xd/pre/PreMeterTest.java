package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyMeter;
import com.xd.pre.modules.myeletric.domain.MyMeterTenantTb;
import com.xd.pre.modules.myeletric.mapper.MyAreaMapper;
import com.xd.pre.modules.myeletric.mapper.MyMeterMapper;
import com.xd.pre.modules.prepay.domain.PreMeter;
import com.xd.pre.modules.prepay.mapper.PreMeterMapper;
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

    @Autowired
    private PreMeterMapper premapper;


    public void testGetRoomMeters(){


   /*     List<MyMeter> mymeterLst = mymapper.getMeterList(1);
        mymeterLst.forEach(e -> {
            System.out.println(e.toString());
        });

        //获取所有电表的表单
        List<MyMeterTenantTb> lst = mymapper.getMeterTenatList();
        lst.forEach(e -> {
            System.out.println(e.toString());
        });

*/
    }

    @Test
    public void testGetPreMeter(){


        List<PreMeter> mymeterLst = premapper.getMeterByID(1);
        mymeterLst.forEach(e -> {
            System.out.println(e.toString());
        });

        //获取所有电表的表单
      /*  List<MyMeterTenantTb> lst = mymapper.getMeterTenatList();
        lst.forEach(e -> {
            System.out.println(e.toString());
        });
*/

    }
}
