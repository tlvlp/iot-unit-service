package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.rpc.MessageFrowardingException;
import com.tlvlp.iot.server.unit.service.rpc.OutgoingMessageForwarder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OutgoingMessageService {

    private static final Logger log = LoggerFactory.getLogger(OutgoingMessageService.class);
    private Properties properties;
    private OutgoingMessageForwarder messageForwarder;

    public OutgoingMessageService(Properties properties, OutgoingMessageForwarder messageForwarder) {
        this.properties = properties;
        this.messageForwarder = messageForwarder;
    }

    public ResponseEntity<String> sendControlMessageToUnit(Unit unit, Map<String, String> payloadMap) throws ResponseStatusException {
       try {
           Message newMessage = new Message()
                   .setTimeArrived(LocalDateTime.now())
                   .setDirection(Message.Direction.OUTGOING)
                   .setTopic(unit.getControlTopic())
                   .setUnitID(unit.getId())
                   .setPayload(payloadMap);
           return messageForwarder.forwardMessage(newMessage);
       } catch (MessageFrowardingException e) {
           log.error("Error sending control message to unit! {}", e.getMessage());
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
       }
    }

    public ResponseEntity<String> sendGlobalStatusRequest() throws ResponseStatusException {
        try {
            Message newMessage = new Message()
                    .setTimeArrived(LocalDateTime.now())
                    .setDirection(Message.Direction.OUTGOING)
                    .setTopic(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST)
                    .setUnitID("global")
                    .setPayload(new HashMap<String, String>());
            return messageForwarder.forwardMessage(newMessage);
        } catch (MessageFrowardingException e) {
            log.error("Error sending global status request! {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
