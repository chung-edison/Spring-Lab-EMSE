package com.emse.spring.faircorp.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomDaoCustom {
    @Query("select r from Room r where r.name=:name")
    Room findByName(@Param("name") String name);
}
