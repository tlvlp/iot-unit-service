package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class LightSensorGl5528 implements Module {

    public static final String REFERENCE = "gl5528";
    private String moduleID;
    private String name;
    private Integer value;


    @Override
    public String toString() {
        return "LightSensorGl5528{" +
                "moduleID='" + moduleID + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LightSensorGl5528)) return false;
        LightSensorGl5528 that = (LightSensorGl5528) o;
        return moduleID.equals(that.moduleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleID);
    }

    public String getModuleID() {
        return moduleID;
    }

    public LightSensorGl5528 setModuleID(String moduleID) {
        this.moduleID = moduleID;
        return this;
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
