package com.emse.spring.faircorp.model.room;

import com.emse.spring.faircorp.model.Status;
import com.emse.spring.faircorp.model.building.BuildingDao;
import com.emse.spring.faircorp.model.light.Light;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController  // (1)
@CrossOrigin
@RequestMapping("/api/rooms") // (2)
@Transactional // (3)
public class RoomController {

    @Autowired
    private final RoomDao roomDao; // (4)
    @Autowired
    private BuildingDao buildingDao;

    public RoomController(RoomDao dao){
        this.roomDao = dao;
    }

    @GetMapping // (5)
    public List<RoomDto> findAll() {
        return roomDao.findAll()
                .stream()
                .map(RoomDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public RoomDto findById(@PathVariable Long id) {
        return roomDao.findById(id).map(room -> new RoomDto(room)).orElse(null);
    }

    @PostMapping
    public RoomDto create(@RequestBody RoomDto dto) {
        Room room = null;
        if (dto.getId() != null) {
            room = roomDao.findById(dto.getId()).orElse(null);
        }

        if (room == null) {
            room = roomDao.save(new Room(dto.getName(), dto.getFloor(), dto.getLights(), buildingDao.getOne(dto.getBuildingId())));
        } else {
            room.setFloor(dto.getFloor());
            room.setLights(dto.getLights());
            room.setName(dto.getName());
            roomDao.save(room);
        }

        return new RoomDto(room);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        roomDao.deleteById(id);
    }

    @PutMapping(path ="/{id}/switchLights")
    public RoomDto switchAllLights(@PathVariable Long id) {
        Room room = roomDao.findById(id).orElseThrow(IllegalArgumentException::new);
        for(Light light:room.getLights()) {
            light.setStatus(light.getStatus() == Status.ON ? Status.OFF : Status.ON);
        }
        return new RoomDto(room);
    }
}
