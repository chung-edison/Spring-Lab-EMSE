package com.emse.spring.faircorp.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDao extends JpaRepository<Room, Long>, RoomDaoCustom {
}
