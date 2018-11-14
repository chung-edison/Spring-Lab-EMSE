package com.emse.spring.faircorp.model;

import java.util.Set;

public class RoomDto {
    private Long id;
    private String name;
    private Integer floor;
    private Set<Light> lights;
    private Long buildingId;

    public RoomDto() {
    }

    public RoomDto(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.floor = room.getFloor();
        this.lights = room.getLights();
        this.buildingId = room.getBuilding().getId();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getFloor() {
        return floor;
    }

    public Set<Light> getLights() {
        return lights;
    }

    public Long getBuildingId() {
        return buildingId;
    }
}
