package com.emse.spring.faircorp.model.tempSensor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TempSensor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer temperature;

    public TempSensor() {
    }

    public TempSensor(Integer temperature) {
        this.temperature = temperature;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }
}
