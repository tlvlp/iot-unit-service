package com.tlvlp.iot.server.unit.service.modules;

import java.time.LocalDateTime;
import java.util.Objects;

public class Relay implements Module {

    public enum State {
        on, off
    }

    private static final String reference = "relay";
    private String name;
    private State state;

    @Override
    public String toString() {
        return "Relay{" +
                "name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relay)) return false;
        Relay relay = (Relay) o;
        return name.equals(relay.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static String getReference() {
        return reference;
    }

    public State getState() {
        return state;
    }

    public Relay setState(State state) {
        this.state = state;
        return this;
    }

    public String getName() {
        return name;
    }

    public Relay setName(String name) {
        this.name = name;
        return this;
    }
}
