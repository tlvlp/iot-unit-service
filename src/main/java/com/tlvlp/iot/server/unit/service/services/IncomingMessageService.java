package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IncomingMessageService {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageService.class);
    private Properties properties;
    private UnitService unitService;

    public IncomingMessageService(Properties properties, UnitService unitService) {
        this.properties = properties;
        this.unitService = unitService;
    }

    public ResponseEntity handleIncomingMessage(Message message) {
        try {
            checkMessageValidity(message);
            String topic = message.getTopic();
            if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_ERROR)) {
                this.unitService.handleUnitError(message);
            } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_INACTIVE)) {
                this.unitService.handleInactiveUnit(message);
            } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS)) {
                this.unitService.handleUnitStatus(message);
            } else {
                throw new IllegalArgumentException(String.format("Unknown topic: %s", topic));
            }
            return new ResponseEntity<String>("ok", HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            log.error("Error processing message! {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void checkMessageValidity(Message message) throws IllegalArgumentException{
        if (message.getUnitID() == null) {
            throw new IllegalArgumentException("Missing UnitID");
        } else if (message.getTopic() == null) {
            throw new IllegalArgumentException("Missing topic");
        } else if (message.getTimeArrived() == null) {
            throw new IllegalArgumentException("Missing timeArrived");
        } else if (message.getPayload().get("name") == null) {
            throw new IllegalArgumentException("Missing name in payload");
        } else if (message.getPayload().get("project") == null) {
            throw new IllegalArgumentException("Missing project in payload");
        }
    }







}
