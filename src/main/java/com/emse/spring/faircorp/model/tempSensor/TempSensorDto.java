package com.emse.spring.faircorp.model.tempSensor;

import com.emse.spring.faircorp.model.tempSensor.TempSensor;

public class TempSensorDto {

    private Long id;
    private Integer temperature;

    public TempSensorDto() {
    }

    public TempSensorDto(TempSensor tempSensor) {
        this.id = tempSensor.getId();
        this.temperature = tempSensor.getTemperature();
    }

    public Long getId() {
        return id;
    }

    public Integer getTemperature() {
        return temperature;
    }
}