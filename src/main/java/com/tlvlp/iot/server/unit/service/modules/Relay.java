package com.tlvlp.iot.server.unit.service.modules;

import java.util.Objects;

public class Relay implements Module {

    public enum State {
        on, off
    }

    public static final String REFERENCE = "relay";
    private String id;
    private String name;
    private State state;

    @Override
    public String toString() {
        return "Relay{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relay)) return false;
        Relay relay = (Relay) o;
        return id.equals(relay.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public Relay setId(String id) {
        this.id = id;
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
