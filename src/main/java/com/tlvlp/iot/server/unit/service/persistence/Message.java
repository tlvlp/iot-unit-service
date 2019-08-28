package com.tlvlp.iot.server.unit.service.persistence;

import java.util.Map;

public class Message {

    private String topic;
    private Map<String, String> payload;

    @Override
    public String toString() {
        return "Message{" +
                ", topic='" + topic + '\'' +
                ", payload=" + payload +
                '}';
    }

    public String getTopic() {
        return topic;
    }

    public Message setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public Message setPayload(Map<String, String> payload) {
        this.payload = payload;
        return this;
    }

}
