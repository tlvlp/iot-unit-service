package com.tlvlp.iot.server.unit.service.rpc;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;


/**
 * An MQTT message sent between the server and the MCUs
 */
@Document(collection = "messages")
public class Message {

    public enum Direction {
        INCOMING, OUTGOING
    }

    @Id
    private LocalDateTime timeArrived;
    private Direction direction;
    private String topic;
    private String unitID;
    private Map<String, String> payload;

    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "timeArrived=" + timeArrived +
                ", direction=" + direction +
                ", topic='" + topic + '\'' +
                ", unitID='" + unitID + '\'' +
                ", payload=" + payload +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeArrived, direction, topic, unitID, payload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return timeArrived.equals(message.timeArrived) &&
                direction == message.direction &&
                topic.equals(message.topic) &&
                unitID.equals(message.unitID) &&
                payload.equals(message.payload);
    }

    public LocalDateTime getTimeArrived() {
        return timeArrived;
    }

    public Message setTimeArrived(LocalDateTime timeArrived) {
        this.timeArrived = timeArrived;
        return this;
    }

    public Direction getDirection() {
        return direction;
    }

    public Message setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Message setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getUnitID() {
        return unitID;
    }

    public Message setUnitID(String unitID) {
        this.unitID = unitID;
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
