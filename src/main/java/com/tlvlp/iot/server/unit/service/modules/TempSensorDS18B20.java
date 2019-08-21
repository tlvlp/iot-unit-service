package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class TempSensorDS18B20 implements Module {

    public static final String REFERENCE = "ds18b20";
    private String id;
    private Integer value;

    @Override
    public String toString() {
        return "TempSensorDS18B20{" +
                "id='" + id + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TempSensorDS18B20)) return false;
        TempSensorDS18B20 that = (TempSensorDS18B20) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public TempSensorDS18B20 setId(String id) {
        this.id = id;
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
