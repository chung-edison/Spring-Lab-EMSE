package com.emse.spring.faircorp.model;

import com.emse.spring.faircorp.model.room.Room;
import com.emse.spring.faircorp.model.room.RoomDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class RoomDaoCustomTest {

    @Autowired
    RoomDao roomDao;

    @Test
    public void findByName() {
        Room room = roomDao.findByName("Room1") ;
        assertThat(room.getName()).isEqualTo("Room1");
        assertThat(room.getFloor()).isEqualTo(1);
        assertThat(room.getId()).isEqualTo(-10L);
    }
}