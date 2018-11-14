package com.emse.spring.faircorp.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BuildingDaoCustom {
    @Query("select l from Building b join b.rooms r join r.lights l where b.id=:id")
    Building findAllLights(@Param("id") Long id);
}
