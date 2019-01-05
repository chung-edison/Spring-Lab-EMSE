package com.emse.spring.faircorp.model;

public class MeasurementDto {

    private String n;
    private String u;
    private int v;

    public MeasurementDto() {
    }

    public MeasurementDto(String n, String u, int v) {
        this.n = n;
        this.u = u;
        this.v = v;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}
