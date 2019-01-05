package com.emse.spring.faircorp.model.tempSensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
@RequestMapping("/api/temperature-sensors") // (2)
@Transactional // (3)
@EnableScheduling
public class TempSensorController {

    @Autowired
    private final TempSensorDao tempSensorDao; // (4)
    @Autowired
    private SimpMessagingTemplate template;

    public TempSensorController(TempSensorDao dao) {
        this.tempSensorDao = dao;
    }

    @GetMapping // (5)
    public List<TempSensorDto> findAll() {
        return tempSensorDao.findAll()
                .stream()
                .map(TempSensorDto::new)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 1000)
    public void webSocketFindAll() {
        this.template.convertAndSend("/topic/temperature-sensors", findAll());
    }

    @GetMapping(path = "/{id}")
    public TempSensorDto findById(@PathVariable @DestinationVariable Long id) {
        return tempSensorDao.findById(id).map(tempSensor -> new TempSensorDto(tempSensor)).orElse(null);
    }

    @Scheduled(fixedRate = 1000)
    public void webSocketUpdateById() {
        for (TempSensorDto tempSensorDto : findAll())
            this.template.convertAndSend("/topic/temperature-sensors/" + tempSensorDto.getId(), findById(tempSensorDto.getId()));
    }

    @PostMapping
    public TempSensorDto create(@RequestBody TempSensorDto dto) {
        TempSensor tempSensor = null;
        if (dto.getId() != null) {
            tempSensor = tempSensorDao.findById(dto.getId()).orElse(null);
        }

        if (tempSensor == null) {
            tempSensor = tempSensorDao.save(new TempSensor(dto.getTemperature()));
        } else {
            tempSensor.setTemperature(dto.getTemperature());
            tempSensorDao.save(tempSensor);
        }

        return new TempSensorDto(tempSensor);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        tempSensorDao.deleteById(id);
    }

    public void updateTemperature(Long id, int temperature) {
        TempSensor tempSensor = tempSensorDao.findById(id).orElseThrow(IllegalArgumentException::new);
        tempSensor.setTemperature(temperature);
        // System.out.println("Sensor " + id + " updated temperature to " + temperature);
    }
}