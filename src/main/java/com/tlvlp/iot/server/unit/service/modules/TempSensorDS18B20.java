package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class TempSensorDS18B20 implements Module {

    private static final String reference = "ds18b20";
    private String name;
    private Integer value;

    @Override
    public String toString() {
        return "TempSensorDS18B20{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TempSensorDS18B20)) return false;
        TempSensorDS18B20 that = (TempSensorDS18B20) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static String getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public TempSensorDS18B20 setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public TempSensorDS18B20 setValue(Integer value) {
        this.value = value;
        return this;
    }
}
