package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class SoilMoistureSensor implements Module {

    public static final String REFERENCE = "somo";
    private String name;
    private Integer value;

    @Override
    public String toString() {
        return "SoilMoistureSensor{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoilMoistureSensor)) return false;
        SoilMoistureSensor that = (SoilMoistureSensor) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public SoilMoistureSensor setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public SoilMoistureSensor setValue(Integer value) {
        this.value = value;
        return this;
    }
}
