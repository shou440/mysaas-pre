package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyArea;
import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.mapper.MyAreaMapper;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreAreaTest {

    @Autowired
    private MyAreaMapper mymapper;

    @Test
    public void testGetAreaByOwner(){


        List<MyArea> myareaLst = mymapper.getAreaByOwner(4);
        myareaLst.forEach(e -> {
            System.out.println(e.toString());
        });

    }
}
