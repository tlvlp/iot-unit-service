package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class IncomingMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageHandler.class);
    private Properties properties;
    private UnitService unitService;
    private UnitLogService unitLogService;

    public static String RESPONSE_TYPE = "type";
    public static String RESPONSE_OBJECT = "object";


    public IncomingMessageHandler(Properties properties, UnitService unitService, UnitLogService unitLogService) {
        this.properties = properties;
        this.unitService = unitService;
        this.unitLogService = unitLogService;
    }

    public HashMap<String, Object> handleIncomingMessage(Message message) throws MessageProcessingException {
        try {
            checkPayloadValidity(message);
            var topic = message.getTopic();
            HashMap<String, Object> responseMap = new HashMap<>();
            if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_ERROR())) {
                responseMap.put(RESPONSE_TYPE, "error");
                responseMap.put(RESPONSE_OBJECT, handleUnitError(message));
            } else if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_INACTIVE())) {
                responseMap.put(RESPONSE_TYPE, "inactive");
                responseMap.put(RESPONSE_OBJECT, handleInactiveUnit(message));
            } else if (topic.equals(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS())) {
                responseMap.put(RESPONSE_TYPE, "status");
                responseMap.put(RESPONSE_OBJECT, handleUnitStatusChange(message));
            } else {
                throw new MessageProcessingException(String.format("Unknown topic: %s", topic));
            }
            return responseMap;
        } catch (MessageProcessingException | UnitProcessingException e) {
            var err = String.format("Error processing message! %s", e.getMessage());
            log.error(err);
            throw new MessageProcessingException(err);
        }
    }

    //
    private void checkPayloadValidity(Message message) throws MessageProcessingException {
        if (message.getPayload().get("unitID") == null) {
            throw new MessageProcessingException("Missing UnitID");
        } else if (message.getPayload().get("name") == null) {
            throw new MessageProcessingException("Missing name in payload");
        } else if (message.getPayload().get("project") == null) {
            throw new MessageProcessingException("Missing project in payload");
        }
    }

    private UnitLog handleUnitError(Message message) throws MessageProcessingException {
        if (message.getPayload().get("error") == null) {
            throw new MessageProcessingException("Missing error message in unit error payload");
        }
        UnitLog unitLog = unitLogService.saveUnitLogErrorFromMessage(message);
        log.info("Unit error message: {}", unitLog);
        return unitLog;
    }

    private Unit handleInactiveUnit(Message message) throws UnitProcessingException {
        Optional<Unit> unitDB = unitService.getUnitByID(message.getPayload().get("unitID"));
        if (unitDB.isPresent()) {
            Unit unit = unitDB.get();
            unit.setActive(false);
            var savedUnit = unitService.saveUnit(unit);
            unitLogService.saveUnitLogInactiveFromMessage(message);
            log.info("Unit is inactive: UnitID:{} Project:{} Name:{}",
                    unit.getUnitID(), unit.getProject(), unit.getName());
            return savedUnit;
        } else {
            throw new UnitProcessingException("Unit doesn't exist so cannot be set to inactive status");
        }
    }

    private Unit handleUnitStatusChange(Message message) throws MessageProcessingException {
        Optional<Unit> unitDB = unitService.getUnitByID(message.getPayload().get("unitID"));
        if (unitDB.isPresent()) {
            return unitService.updateUnitFromMessage(unitDB.get(), message);
        } else {
            Unit newUnit = unitService.createUnitFromMessage(message);
            unitLogService.saveUnitFirstCreationFromMessage(message);
            return newUnit;
        }
    }


}
