package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class TempSensorDS18B20 implements Module {

    public static final String REFERENCE = "ds18b20";
    private String moduleID;
    private String name;
    private Integer value;
    private String unitID;

    @Override
    public String toString() {
        return "TempSensorDS18B20{" +
                "moduleID='" + moduleID + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", unitID='" + unitID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TempSensorDS18B20)) return false;
        TempSensorDS18B20 that = (TempSensorDS18B20) o;
        return moduleID.equals(that.moduleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleID);
    }

    public String getModuleID() {
        return moduleID;
    }

    public TempSensorDS18B20 setModuleID(String moduleID) {
        this.moduleID = moduleID;
        return this;
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

    public String getUnitID() {
        return unitID;
    }

    public TempSensorDS18B20 setUnitID(String unitID) {
        this.unitID = unitID;
        return this;
    }
}
