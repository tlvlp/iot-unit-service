package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class SoilMoistureSensor implements Module {

    public static final String REFERENCE = "somo";
    private String id;
    private String name;
    private Integer value;

    @Override
    public String toString() {
        return "SoilMoistureSensor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoilMoistureSensor)) return false;
        SoilMoistureSensor that = (SoilMoistureSensor) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public SoilMoistureSensor setId(String id) {
        this.id = id;
        return this;
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
