package com.tlvlp.iot.server.unit.service.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * A Microcontroller Unit (MCU)
 */
@Document
public class Unit {

    @Id
    private String unitID;
    @NotBlank
    private String name;
    @NotBlank
    private String project;
    @NotNull
    private Boolean active;
    @NotBlank
    private String controlTopic;
    @PastOrPresent
    private LocalDateTime lastSeen;
    @NotNull
    private Set<Module> modules;
    @NotNull
    private Set<String> scheduledEvents;

    public Unit() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;
        Unit unit = (Unit) o;
        return unitID.equals(unit.unitID) &&
                name.equals(unit.name) &&
                project.equals(unit.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitID, name, project);
    }

    @Override
    public String toString() {
        return "Unit{" +
                "unitID='" + unitID + '\'' +
                ", name='" + name + '\'' +
                ", project='" + project + '\'' +
                ", active=" + active +
                ", controlTopic='" + controlTopic + '\'' +
                ", lastSeen=" + lastSeen +
                ", modules=" + modules +
                ", scheduledEvents=" + scheduledEvents +
                '}';
    }

    public String getUnitID() {
        return unitID;
    }

    public Unit setUnitID(String unitID) {
        this.unitID = unitID;
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

    public Boolean isActive() {
        return active;
    }

    public Unit setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public String getControlTopic() {
        return controlTopic;
    }

    public Unit setControlTopic(String controlTopic) {
        this.controlTopic = controlTopic;
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

    public Set<String> getScheduledEvents() {
        return scheduledEvents;
    }

    public Unit setScheduledEvents(Set<String> scheduledEvents) {
        this.scheduledEvents = scheduledEvents;
        return this;
    }
}