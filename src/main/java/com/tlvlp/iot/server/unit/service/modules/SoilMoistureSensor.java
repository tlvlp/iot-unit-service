package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class SoilMoistureSensor implements Module {

    public static final String REFERENCE = "somo";
    private String moduleID;
    private String name;
    private Integer value;
    private String unitID;

    @Override
    public String toString() {
        return "SoilMoistureSensor{" +
                "moduleID='" + moduleID + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", unitID='" + unitID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoilMoistureSensor)) return false;
        SoilMoistureSensor that = (SoilMoistureSensor) o;
        return moduleID.equals(that.moduleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleID);
    }

    public String getModuleID() {
        return moduleID;
    }

    public SoilMoistureSensor setModuleID(String moduleID) {
        this.moduleID = moduleID;
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

    public String getUnitID() {
        return unitID;
    }

    public SoilMoistureSensor setUnitID(String unitID) {
        this.unitID = unitID;
        return this;
    }
}
