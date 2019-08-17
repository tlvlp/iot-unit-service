package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class SoilMoistureSensor implements Module {

    private static final String reference = "somo";
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
        return name.equals(that.name) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    public static String getReference() {
        return reference;
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
