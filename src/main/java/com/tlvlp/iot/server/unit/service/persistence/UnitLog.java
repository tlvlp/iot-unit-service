package com.tlvlp.iot.server.unit.service.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document
public class UnitLog {

    @Id
    private String unitID;
    private String name;
    private String project;
    private LocalDateTime arrived;
    private String logEntry;

    @Override
    public String toString() {
        return "UnitError{" +
                "unitID='" + unitID + '\'' +
                ", name='" + name + '\'' +
                ", project='" + project + '\'' +
                ", arrived=" + arrived +
                ", error='" + logEntry + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitLog)) return false;
        UnitLog unitLog = (UnitLog) o;
        return unitID.equals(unitLog.unitID) &&
                name.equals(unitLog.name) &&
                project.equals(unitLog.project) &&
                arrived.equals(unitLog.arrived) &&
                logEntry.equals(unitLog.logEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitID, name, project, arrived, logEntry);
    }

    public String getUnitID() {
        return unitID;
    }

    public UnitLog setUnitID(String unitID) {
        this.unitID = unitID;
        return this;
    }

    public String getName() {
        return name;
    }

    public UnitLog setName(String name) {
        this.name = name;
        return this;
    }

    public String getProject() {
        return project;
    }

    public UnitLog setProject(String project) {
        this.project = project;
        return this;
    }

    public LocalDateTime getArrived() {
        return arrived;
    }

    public UnitLog setArrived(LocalDateTime arrived) {
        this.arrived = arrived;
        return this;
    }

    public String getLogEntry() {
        return logEntry;
    }

    public UnitLog setLogEntry(String logEntry) {
        this.logEntry = logEntry;
        return this;
    }
}