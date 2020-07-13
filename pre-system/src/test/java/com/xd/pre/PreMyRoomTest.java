package com.xd.pre;

import com.xd.pre.modules.myeletric.domain.MyRoom;
import com.xd.pre.modules.myeletric.dto.MyRoomDto;
import com.xd.pre.modules.myeletric.dto.MyRoomLessorDto;
import com.xd.pre.modules.myeletric.dto.MyRoomTenantDto;
import com.xd.pre.modules.myeletric.mapper.MyRoomMapper;
import com.xd.pre.modules.myeletric.mapper.MyRoomTenantMapper;
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
public class PreMyRoomTest {

    @Autowired
   private MyRoomMapper mymapper;

    @Autowired
    private IMyRoomService myRoomService;

    @Test
    public void getroom(){


  /*      List<MyRoom> myrooms = mymapper.getRoomInfo(1);
        myrooms.forEach(e -> {
            System.out.println(e.toString());
        });



        MyRoomLessorDto roomlessor = new MyRoomLessorDto();
        roomlessor.setRoom_id(1);
        roomlessor.setPeriod_start_time(new Timestamp(System.currentTimeMillis()));


        if (0 == myRoomService.LessorRoom(roomlessor))
        {

        }


       */

        int kkk = 0;

    }

    @Test
    public void UnLessorRoom(){

        String sErr = "";
        MyRoomLessorDto roomLessorDto = new MyRoomLessorDto();
        roomLessorDto.setRoom_id(1);
        myRoomService.UnLessorRoom(roomLessorDto,sErr);



        int kkk = 0;

    }
}
