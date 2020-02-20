package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreMyRoomTest {

    @Autowired
   private MyRoomMapper mymapper;


    @Test
    public void getroom(){


        List<MyRoom> myrooms = mymapper.getRoomInfo(1);
        myrooms.forEach(e -> {
            System.out.println(e.toString());
        });

       /* MyRoomDto room = new MyRoomDto();
        room.setRoom_name("测试房间");
        room.setTenant_fee(10);
        room.setTenant_manage_fee(10);
        room.setTenant_other_fee(10);
        mymapper.updateRoominfo(room);
        */

        MyRoomDto room = new MyRoomDto();
        room.setRoom_name("测试房间1");
        room.setArea_id(1);
        mymapper.createNewRoom(room);

        int kkk = 0;

    }
}
