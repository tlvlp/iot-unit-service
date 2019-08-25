package com.tlvlp.iot.server.unit.service.modules;

import org.springframework.data.annotation.Id;

import java.util.Objects;

public class Relay implements Module {

    public enum State {
        on, off
    }

    public static final String REFERENCE = "relay";
    @Id
    private String moduleID;
    private String name;
    private State state;


    @Override
    public String toString() {
        return "Relay{" +
                "moduleID='" + moduleID + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relay)) return false;
        Relay relay = (Relay) o;
        return moduleID.equals(relay.moduleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleID);
    }

    public String getModuleID() {
        return moduleID;
    }

    public Relay setModuleID(String moduleID) {
        this.moduleID = moduleID;
        return this;
    }

    public String getName() {
        return name;
    }

    public Relay setName(String name) {
        this.name = name;
        return this;
    }

    public State getState() {
        return state;
    }

    public Relay setState(State state) {
        this.state = state;
        return this;
    }
}
