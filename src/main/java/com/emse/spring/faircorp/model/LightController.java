package com.emse.spring.faircorp.model;

import com.emse.spring.faircorp.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController  // (1)
@CrossOrigin
@RequestMapping("/api/lights") // (2)
@Transactional // (3)
@EnableScheduling
public class LightController {

    @Autowired
    private final LightDao lightDao; // (4)
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private SimpMessagingTemplate template;

    public LightController(LightDao dao) {
        this.lightDao = dao;
    }

    @GetMapping // (5)
    public List<LightDto> findAll() {
        return lightDao.findAll()
                .stream()
                .map(LightDto::new)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 1000)
    public void webSocketFindAll() {
        this.template.convertAndSend("/topic/lights", findAll());
    }

    @GetMapping(path = "/{id}")
    public LightDto findById(@PathVariable @DestinationVariable Long id) {
        return lightDao.findById(id).map(light -> new LightDto(light)).orElse(null);
    }

    @Scheduled(fixedRate = 1000)
    public void webSocketUpdateById() {
        for (LightDto light : findAll()) this.template.convertAndSend("/topic/lights/" + light.getId(), findById(light.getId()));
    }

    @PutMapping(path = "/{id}/switch")
    public LightDto switchStatus(@PathVariable Long id) {
        Light light = lightDao.findById(id).orElseThrow(IllegalArgumentException::new);
        light.setStatus(light.getStatus() == Status.ON ? Status.OFF : Status.ON);
        // publish to sensor/{id}/CMD [ON, OFF]
        MqttGateway mqttGateway = context.getBean(MqttGateway.class);
        mqttGateway.sendToMqtt(MessageBuilder.withPayload(light.getStatus().toString()).setHeader(MqttHeaders.TOPIC, "sensor/" + id + "/CMD").build());
        return new LightDto(light);
    }

    @PostMapping
    public LightDto create(@RequestBody LightDto dto) {
        Light light = null;
        if (dto.getId() != null) {
            light = lightDao.findById(dto.getId()).orElse(null);
        }

        if (light == null) {
            light = lightDao.save(new Light(dto.getLevel(), dto.getStatus(), roomDao.getOne(dto.getRoomId())));
        } else {
            light.setLevel(dto.getLevel());
            light.setStatus(dto.getStatus());
            lightDao.save(light);
        }

        return new LightDto(light);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        lightDao.deleteById(id);
    }

    public void updateLightLevel(Long id, int level) {
        Light light = lightDao.findById(id).orElseThrow(IllegalArgumentException::new);
        light.setLevel(level);
        // System.out.println("Sensor " + id + " updated Light level to " + level);
    }
}