package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.rpc.OutgoingMessageForwarder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OutgoingMessageService {

    private OutgoingMessageForwarder messageSender;
    private Properties properties;

    public OutgoingMessageService(OutgoingMessageForwarder messageSender, Properties properties) {
        this.messageSender = messageSender;
        this.properties = properties;
    }

    public void sendControlMessageToUnit(Unit unit, Map<String, String> payloadMap) {
        Message newMessage = new Message()
                .setTimeArrived(LocalDateTime.now())
                .setDirection(Message.Direction.OUTGOING)
                .setTopic(unit.getControlTopic())
                .setUnitID(unit.getId())
                .setPayload(payloadMap);
        messageSender.forwardMessage(newMessage);

    }

    public void sendGlobalStatusRequest() {
        Message newMessage = new Message()
                .setTimeArrived(LocalDateTime.now())
                .setDirection(Message.Direction.OUTGOING)
                .setTopic(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST)
                .setUnitID("global")
                .setPayload(new HashMap<String, String>());
        messageSender.forwardMessage(newMessage);
    }
}
