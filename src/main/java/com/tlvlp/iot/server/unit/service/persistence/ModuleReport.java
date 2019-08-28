package com.tlvlp.iot.server.unit.service.persistence;

public class ModuleReport {

    private String moduleID;
    private Double value;
    private String unitID;

    @Override
    public String toString() {
        return "Module{" +
                "moduleID='" + moduleID + '\'' +
                ", value=" + value +
                ", unitID='" + unitID + '\'' +
                '}';
    }


    public String getModuleID() {
        return moduleID;
    }

    public ModuleReport setModuleID(String moduleID) {
        this.moduleID = moduleID;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public ModuleReport setValue(Double value) {
        this.value = value;
        return this;
    }

    public String getUnitID() {
        return unitID;
    }

    public ModuleReport setUnitID(String unitID) {
        this.unitID = unitID;
        return this;
    }
}
