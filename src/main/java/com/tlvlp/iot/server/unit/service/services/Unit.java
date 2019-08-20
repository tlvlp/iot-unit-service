package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.modules.Module;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A Microcontroller Unit (MCU)
 */
@Document
public class Unit {
    
    @Id
    private String id;
    private String name;
    private String project;
    private Boolean active;
    private LocalDateTime lastSeen;
    private Set<Module> modules;

    public Unit() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;
        Unit unit = (Unit) o;
        return id.equals(unit.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", project='" + project + '\'' +
                ", active=" + active +
                ", lastSeen=" + lastSeen +
                ", modules=" + modules +
                '}';
    }

    public String getId() {
        return id;
    }

    public Unit setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Unit setName(String name) {
        this.name = name;
        return this;
    }

    public String getProject() {
        return project;
    }

    public Unit setProject(String project) {
        this.project = project;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public Unit setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public Unit setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
        return this;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public Unit setModules(Set<Module> modules) {
        this.modules = modules;
        return this;
    }
}