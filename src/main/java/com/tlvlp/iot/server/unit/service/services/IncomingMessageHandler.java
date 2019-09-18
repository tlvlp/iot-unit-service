package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@Service
public class IncomingMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageHandler.class);
    private Properties properties;
    private UnitService unitService;
    private UnitLogService unitLogService;


    public IncomingMessageHandler(Properties properties, UnitService unitService, UnitLogService unitLogService) {
        this.properties = properties;
        this.unitService = unitService;
        this.unitLogService = unitLogService;
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
                return new ResponseEntity(HttpStatus.OK);
            } else if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS())) {
                Unit updatedUnit = handleUnitStatusChange(message);
                responseMap.put("type", "status");
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
        Optional<Unit> unitDB = unitService.getUnitByID(message.getPayload().get("unitID"));
        if (unitDB.isPresent()) {
            return unitService.updateUnitFromMessage(unitDB.get(), message);
        } else {
            return unitService.createUnitFromMessage(message);
        }
    }

    private Optional<UnitLog> handleInactiveUnit(Message message) {
        Optional<Unit> unitDB = unitService.getUnitByID(message.getPayload().get("unitID"));
        if (unitDB.isPresent()) {
            Unit unit = unitDB.get();
            unit.setActive(false);
            unitService.saveUnit(unit);
            log.info("Unit is inactive: UnitID:{} Project:{} Name:{}",
                    unit.getUnitID(), unit.getProject(), unit.getName());
            UnitLog unitLog = unitLogService.saveUnitLogInactiveFromMessage(message);
            return Optional.of(unitLog);
        }
        return Optional.empty();
    }

    private UnitLog handleUnitError(Message message) throws IllegalArgumentException {
        if (message.getPayload().get("error") == null) {
            throw new IllegalArgumentException("Missing error message in unit error payload");
        }
        UnitLog unitLog = unitLogService.saveUnitLogErrorFromMessage(message);
        log.info("Unit error message: {}", unitLog);
        return unitLog;
    }


}
