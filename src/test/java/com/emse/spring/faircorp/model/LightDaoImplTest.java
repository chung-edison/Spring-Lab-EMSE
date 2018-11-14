package com.emse.spring.faircorp.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class LightDaoImplTest {

    @Autowired
    LightDao lightDao;

    @Test
    public void shouldFindOnLights() {
        assertThat(lightDao.findOnLights())
                .hasSize(1)
                .extracting("id", "status")
                .containsExactly(tuple(-1L, Status.ON));
    }
}