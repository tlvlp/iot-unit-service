package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class IncomingMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageHandler.class);
    private Properties properties;
    private UnitLogRepository logRepository;
    private UnitRepository unitRepository;
    private UnitService unitService;


    public IncomingMessageHandler(Properties properties, UnitLogRepository logRepository,
                                  UnitRepository unitRepository, UnitService unitService) {
        this.properties = properties;
        this.logRepository = logRepository;
        this.unitRepository = unitRepository;
        this.unitService = unitService;
    }

    public ResponseEntity handleIncomingMessage(Message message) throws ResponseStatusException {
        try {
            checkMessageValidity(message);
            String topic = message.getTopic();
            HashMap<String, Object> responseMap = new HashMap<>();
            if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_ERROR())) {
                UnitLog unitLog = handleUnitError(message);
                responseMap.put("type", "error");
                responseMap.put("content", unitLog);
                return new ResponseEntity<HashMap>(responseMap, HttpStatus.ACCEPTED);
            } else if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_INACTIVE())) {
                Optional<UnitLog> unitLog = handleInactiveUnit(message);
                if (unitLog.isPresent()) {
                    responseMap.put("type", "inactive");
                    responseMap.put("content", unitLog);
                    return new ResponseEntity<HashMap>(responseMap, HttpStatus.ACCEPTED);
                }
                return new ResponseEntity(HttpStatus.ACCEPTED);
            } else if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS())) {
                Unit updatedUnit = handleUnitStatusChange(message);
                responseMap.put("type", "error");
                responseMap.put("content", updatedUnit);
                return new ResponseEntity<HashMap>(responseMap, HttpStatus.ACCEPTED);
            } else {
                throw new IllegalArgumentException(String.format("Unknown topic: %s", topic));
            }

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

    private Unit handleUnitStatusChange(Message message) {
        Optional<Unit> unitDB = unitRepository.findById(message.getPayload().get("unitID"));
        Unit unitUpdate;
        if (unitDB.isPresent()) {
            unitUpdate = unitService.updateUnitFromMessage(unitDB.get(), message);
        } else {
            unitUpdate = unitService.createUnitFromMessage(message);
        }
        return unitUpdate;
    }

    private Optional<UnitLog> handleInactiveUnit(Message message) {
        Optional<Unit> unitDB = unitRepository.findById(message.getPayload().get("unitID"));
        if (unitDB.isPresent()) {
            Unit unit = unitDB.get();
            unit.setActive(false);
            unitRepository.save(unit);
            UnitLog unitLog = new UnitLog()
                    .setUnitID(message.getPayload().get("unitID"))
                    .setProject(message.getPayload().get("project"))
                    .setName(message.getPayload().get("name"))
                    .setArrived(LocalDateTime.now())
                    .setLogEntry("Unit became inactive");
            logRepository.save(unitLog);
            log.info("Unit is inactive: UnitID:{} Project:{} Name:{}",
                    unit.getUnitID(), unit.getProject(), unit.getName());
            return Optional.of(unitLog);
        }
        return Optional.empty();
    }

    private UnitLog handleUnitError(Message message) throws IllegalArgumentException {
        if (message.getPayload().get("error") == null) {
            throw new IllegalArgumentException("Missing error message in unit error payload");
        }
        UnitLog unitLog = new UnitLog()
                .setUnitID(message.getPayload().get("unitID"))
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setArrived(LocalDateTime.now())
                .setLogEntry(message.getPayload().get("error"));
        logRepository.save(unitLog);
        log.info("Unit error message: {}", unitLog);
        return unitLog;
    }


}
