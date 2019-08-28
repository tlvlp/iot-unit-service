package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class IncomingMessageService {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageService.class);
    private Properties properties;
    private UnitErrorRepository errorRepository;
    private UnitRepository unitRepository;
    private UnitService unitService;


    public IncomingMessageService(Properties properties, UnitErrorRepository errorRepository,
                                  UnitRepository unitRepository, UnitService unitService) {
        this.properties = properties;
        this.errorRepository = errorRepository;
        this.unitRepository = unitRepository;
        this.unitService = unitService;
    }

    public ResponseEntity handleIncomingMessage(Message message) throws ResponseStatusException {
        try {
            checkMessageValidity(message);
            String topic = message.getTopic();
            if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_ERROR)) {
                handleUnitError(message);
            } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_INACTIVE)) {
                handleInactiveUnit(message);
            } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS)) {
                handleUnitStatusChange(message);
            } else {
                throw new IllegalArgumentException(String.format("Unknown topic: %s", topic));
            }
            return new ResponseEntity<String>("ok", HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            log.error("Error processing message! {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void checkMessageValidity(Message message) throws IllegalArgumentException {
        if (message.getPayload().get("unitID") == null) {
            throw new IllegalArgumentException("Missing UnitID");
        } else if (message.getTopic() == null) {
            throw new IllegalArgumentException("Missing topic");
        } else if (message.getPayload().get("name") == null) {
            throw new IllegalArgumentException("Missing name in payload");
        } else if (message.getPayload().get("project") == null) {
            throw new IllegalArgumentException("Missing project in payload");
        }
    }

    private void handleInactiveUnit(Message message) {
        Optional<Unit> unitDB = unitRepository.findById(message.getPayload().get("unitID"));
        if (unitDB.isPresent()) {
            Unit unit = unitDB.get();
            unit.setActive(false);
            unitRepository.save(unit);
            log.info("Unit is inactive: UnitID:{} Project:{} Name:{}",
                    unit.getUnitID(), unit.getProject(), unit.getName());
        }

    }

    private void handleUnitStatusChange(Message message) {
        Optional<Unit> unitDB = unitRepository.findById(message.getPayload().get("unitID"));
        Unit unitUpdate;
        if (unitDB.isPresent()) {
            unitUpdate = unitService.updateUnitFromMessage(unitDB.get(), message);
        } else {
            unitUpdate = unitService.createUnitFromMessage(message);
        }
        unitService.sendUnitUpdateToReporting(unitUpdate);
    }


    private void handleUnitError(Message message) throws IllegalArgumentException {
        if (message.getPayload().get("error") == null) {
            throw new IllegalArgumentException("Missing error message in unit error payload");
        }
        UnitError unitError = new UnitError()
                .setUnitID(message.getPayload().get("unitID"))
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setError(message.getPayload().get("error"));
        errorRepository.save(unitError);
        log.info("Unit error message: {}", unitError);
    }


}
