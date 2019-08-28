package com.tlvlp.iot.server.unit.service.persistence;

import java.util.Objects;

public class Module {

    private String moduleID;
    private String module;
    private String name;
    private Double value;
    private String unitID;

    @Override
    public String toString() {
        return "Module{" +
                "moduleID='" + moduleID + '\'' +
                ", module='" + module + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", unitID='" + unitID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        Module module = (Module) o;
        return Objects.equals(moduleID, module.moduleID) &&
                Objects.equals(this.module, module.module) &&
                Objects.equals(name, module.name) &&
                Objects.equals(value, module.value) &&
                Objects.equals(unitID, module.unitID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleID, module, name, value, unitID);
    }

    public String getModuleID() {
        return moduleID;
    }

    public Module setModuleID(String moduleID) {
        this.moduleID = moduleID;
        return this;
    }

    public String getModule() {
        return module;
    }

    public Module setModule(String module) {
        this.module = module;
        return this;
    }

    public String getName() {
        return name;
    }

    public Module setName(String name) {
        this.name = name;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public Module setValue(Double value) {
        this.value = value;
        return this;
    }

    public String getUnitID() {
        return unitID;
    }

    public Module setUnitID(String unitID) {
        this.unitID = unitID;
        return this;
    }
}
