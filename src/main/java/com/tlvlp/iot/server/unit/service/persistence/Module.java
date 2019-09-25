package com.tlvlp.iot.server.unit.service.persistence;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Module {

    @NotBlank
    private String unitID;
    @NotBlank
    private String moduleID;
    @NotNull
    private Double value;

    @Override
    public String toString() {
        return "Module{" +
                "unitID='" + unitID + '\'' +
                ", moduleID='" + moduleID + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        Module module = (Module) o;
        return Objects.equals(moduleID, module.moduleID) &&
                Objects.equals(unitID, module.unitID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleID, unitID);
    }

    public String getModuleID() {
        return moduleID;
    }

    public Module setModuleID(String moduleID) {
        this.moduleID = moduleID;
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
