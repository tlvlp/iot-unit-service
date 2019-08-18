package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class LightSensorGl5528 implements Module {

    public static final String REFERENCE = "gl5528";
    private String name;
    private Integer value;


    @Override
    public String toString() {
        return "LightSensorGl5528{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LightSensorGl5528)) return false;
        LightSensorGl5528 that = (LightSensorGl5528) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public LightSensorGl5528 setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public LightSensorGl5528 setValue(Integer value) {
        this.value = value;
        return this;
    }
}
