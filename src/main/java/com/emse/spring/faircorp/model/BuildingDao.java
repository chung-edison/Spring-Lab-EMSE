package com.emse.spring.faircorp.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingDao extends JpaRepository<Building, Long>, BuildingDaoCustom {
}
