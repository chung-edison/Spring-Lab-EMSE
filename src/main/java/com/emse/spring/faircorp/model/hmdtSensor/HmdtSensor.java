package com.emse.spring.faircorp.model.hmdtSensor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class HmdtSensor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer humidity;

    public HmdtSensor() {
    }

    public HmdtSensor(Integer humidity) {
        this.humidity = humidity;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }
}
