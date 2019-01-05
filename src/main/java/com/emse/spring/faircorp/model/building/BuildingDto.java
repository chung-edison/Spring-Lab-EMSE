package com.emse.spring.faircorp.model.building;

import com.emse.spring.faircorp.model.room.Room;

import java.util.Set;

public class BuildingDto {
    private Long id;
    private String name;
    private Set<Room> rooms;

    public BuildingDto() {
    }

    public BuildingDto(Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.rooms = building.getRooms();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Room> getRooms() {
        return rooms;
    }
}
