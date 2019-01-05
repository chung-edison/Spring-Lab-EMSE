package com.emse.spring.faircorp.model.hmdtSensor;

import com.emse.spring.faircorp.MqttGateway;
import com.emse.spring.faircorp.model.Status;
import com.emse.spring.faircorp.model.hmdtSensor.HmdtSensor;
import com.emse.spring.faircorp.model.hmdtSensor.HmdtSensorDao;
import com.emse.spring.faircorp.model.hmdtSensor.HmdtSensorDto;
import com.emse.spring.faircorp.model.room.RoomDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController  // (1)
@CrossOrigin
@RequestMapping("/api/humidity-sensors") // (2)
@Transactional // (3)
@EnableScheduling
public class HmdtSensorController {

    @Autowired
    private final HmdtSensorDao hmdtSensorDao; // (4)
    @Autowired
    private SimpMessagingTemplate template;

    public HmdtSensorController(HmdtSensorDao dao) {
        this.hmdtSensorDao = dao;
    }

    @GetMapping // (5)
    public List<HmdtSensorDto> findAll() {
        return hmdtSensorDao.findAll()
                .stream()
                .map(HmdtSensorDto::new)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 1000)
    public void webSocketFindAll() {
        this.template.convertAndSend("/topic/humidity-sensors", findAll());
    }

    @GetMapping(path = "/{id}")
    public HmdtSensorDto findById(@PathVariable @DestinationVariable Long id) {
        return hmdtSensorDao.findById(id).map(hmdtSensor -> new HmdtSensorDto(hmdtSensor)).orElse(null);
    }

    @Scheduled(fixedRate = 1000)
    public void webSocketUpdateById() {
        for (HmdtSensorDto hmdtSensor : findAll())
            this.template.convertAndSend("/topic/humidity-sensors/" + hmdtSensor.getId(), findById(hmdtSensor.getId()));
    }

    @PostMapping
    public HmdtSensorDto create(@RequestBody HmdtSensorDto dto) {
        HmdtSensor hmdtSensor = null;
        if (dto.getId() != null) {
            hmdtSensor = hmdtSensorDao.findById(dto.getId()).orElse(null);
        }

        if (hmdtSensor == null) {
            hmdtSensor = hmdtSensorDao.save(new HmdtSensor(dto.getHumidity()));
        } else {
            hmdtSensor.setHumidity(dto.getHumidity());
            hmdtSensorDao.save(hmdtSensor);
        }

        return new HmdtSensorDto(hmdtSensor);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        hmdtSensorDao.deleteById(id);
    }

    public void updateHumidity(Long id, int humidity) {
        HmdtSensor hmdtSensor = hmdtSensorDao.findById(id).orElseThrow(IllegalArgumentException::new);
        hmdtSensor.setHumidity(humidity);
        // System.out.println("Sensor " + id + " updated humidity to " + humidity);
    }
}