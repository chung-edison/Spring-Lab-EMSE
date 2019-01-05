package com.emse.spring.faircorp.model.hmdtSensor;

public class HmdtSensorDto {

    private Long id;
    private Integer humidity;

    public HmdtSensorDto() {
    }

    public HmdtSensorDto(HmdtSensor hmdtSensor) {
        this.id = hmdtSensor.getId();
        this.humidity = hmdtSensor.getHumidity();
    }

    public Long getId() {
        return id;
    }

    public Integer getHumidity() {
        return humidity;
    }
}