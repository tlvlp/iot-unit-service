package com.tlvlp.iot.server.unit.service.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Objects;

@Document
public class UnitLog {

    @Id
    private String logID;
    @NotBlank
    private String unitID;
    @NotBlank
    private String name;
    @NotBlank
    private String project;
    @PastOrPresent
    private LocalDateTime arrived;
    @NotBlank
    private String logEntry;

    @Override
    public String toString() {
        return "UnitLog{" +
                "logID='" + logID + '\'' +
                ", unitID='" + unitID + '\'' +
                ", name='" + name + '\'' +
                ", project='" + project + '\'' +
                ", arrived=" + arrived +
                ", logEntry='" + logEntry + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitLog unitLog = (UnitLog) o;
        return logID.equals(unitLog.logID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logID);
    }

    public String getLogID() {
        return logID;
    }

    public UnitLog setLogID(String logID) {
        this.logID = logID;
        return this;
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