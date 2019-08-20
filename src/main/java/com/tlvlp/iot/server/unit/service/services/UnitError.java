package com.tlvlp.iot.server.unit.service.services;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * For persisting error messages sent by the MCUs.
 */
@Document
public class UnitError {

    @Id
    private String id;
    private String name;
    private String project;
    private LocalDateTime arrived;
    private String error;

    @Override
    public String toString() {
        return "UnitError{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", project='" + project + '\'' +
                ", arrived=" + arrived +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitError)) return false;
        UnitError unitError = (UnitError) o;
        return id.equals(unitError.id) &&
                name.equals(unitError.name) &&
                project.equals(unitError.project) &&
                arrived.equals(unitError.arrived) &&
                error.equals(unitError.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, project, arrived, error);
    }

    public String getId() {
        return id;
    }

    public UnitError setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UnitError setName(String name) {
        this.name = name;
        return this;
    }

    public String getProject() {
        return project;
    }

    public UnitError setProject(String project) {
        this.project = project;
        return this;
    }

    public LocalDateTime getArrived() {
        return arrived;
    }

    public UnitError setArrived(LocalDateTime arrived) {
        this.arrived = arrived;
        return this;
    }

    public String getError() {
        return error;
    }

    public UnitError setError(String error) {
        this.error = error;
        return this;
    }
}