package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.modules.*;
import com.tlvlp.iot.server.unit.service.persistence.*;
import com.tlvlp.iot.server.unit.service.rpc.UnitUpdateReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class IncomingMessageService {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageService.class);
    private Properties properties;
    private UnitErrorRepository errorRepository;
    private UnitRepository unitRepository;
    private UnitUpdateReporter unitUpdateReporter;

    public IncomingMessageService(Properties properties, UnitRepository unitRepository,
                                  UnitErrorRepository errorRepository, UnitUpdateReporter unitUpdateReporter) {
        this.properties = properties;
        this.unitRepository = unitRepository;
        this.errorRepository = errorRepository;
        this.unitUpdateReporter = unitUpdateReporter;
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
        } else if (message.getTimeArrived() == null) {
            throw new IllegalArgumentException("Missing timeArrived");
        } else if (message.getPayload().get("name") == null) {
            throw new IllegalArgumentException("Missing name in payload");
        } else if (message.getPayload().get("project") == null) {
            throw new IllegalArgumentException("Missing project in payload");
        }
    }

    private void handleInactiveUnit(Message message) {
        Optional<Unit> unitDBOpt = unitRepository.findById(message.getPayload().get("unitID"));
        if (unitDBOpt.isPresent()) {
            Unit unitDB = unitDBOpt.get();
            unitDB.setActive(false);
            unitRepository.save(unitDB);
            log.info("Unit is inactive: UnitID:{} Project:{} Name:{}",
                    unitDB.getId(), unitDB.getProject(), unitDB.getName());
        }

    }

    private void handleUnitStatusChange(Message message) {
        Optional<Unit> unitDB = unitRepository.findById(message.getPayload().get("unitID"));
        Unit unitUpdate;
        if (unitDB.isPresent()) {
            unitUpdate = updateUnit(unitDB.get(), message);
        } else {
            unitUpdate = createUnit(message);
        }
        sendUnitToReporting(unitUpdate);
    }

    private Unit createUnit(Message message) {
        String unitID = message.getPayload().get("unitID");
        Unit newUnit = new Unit()
                .setId(unitID)
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setControlTopic(getUnitControlTopic(unitID))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(parseModulesFromPayload(message.getPayload(), unitID));
        unitRepository.save(newUnit);
        log.info("Added new unit: {}", newUnit);
        return newUnit;
    }

    private String getUnitControlTopic(String unitID) {
        return String.format("/units/%s/control", unitID);
    }

    private Unit updateUnit(Unit unit, Message message) {
        Set<Module> originalModules = unit.getModules();
        unit
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(parseModulesFromPayload(message.getPayload(), unit.getId()));
        unitRepository.save(unit);
        logModuleChanges(unit.getId(), originalModules, unit.getModules());
        log.info("Updated unit: {}", unit);
        return unit;
    }

    private void logModuleChanges(String unitID, Set<Module> originalModules, Set<Module> newModules) {
        Set<Module> addedModules = new HashSet<>(newModules);
        addedModules.removeAll(originalModules);
        if (!addedModules.isEmpty()) {
            log.info("New modules have been added! UnitID:{} Modules:{}", unitID, addedModules);
        }
        Set<Module> removedModules = new HashSet<>(originalModules);
        removedModules.removeAll(newModules);
        if (!removedModules.isEmpty()) {
            log.warn("Modules have been removed! UnitID:{} Modules:{}", unitID, removedModules);
        }
    }

    private Set<Module> parseModulesFromPayload(Map<String, String> payload, String unitID)
            throws IllegalArgumentException {
        Set<Module> modules = new HashSet<>();
        Map<String, String> payloadFiltered = filterPayload(payload);
        for (String key : payloadFiltered.keySet()) {
            try {
                String module_ref = key.split("\\|")[0];
                String module_name = key.split("\\|")[1];
                String module_value = payloadFiltered.get(key);
                switch (module_ref) {
                    case Relay.REFERENCE:
                        modules.add(new Relay()
                                .setModuleID(key)
                                .setName(module_name)
                                .setState(module_value.equals("on") ? Relay.State.on : Relay.State.off)
                                .setUnitID(unitID));
                        break;
                    case LightSensorGl5528.REFERENCE:
                        modules.add(new LightSensorGl5528()
                                .setModuleID(key)
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value))
                                .setUnitID(unitID));
                        break;
                    case SoilMoistureSensor.REFERENCE:
                        modules.add(new SoilMoistureSensor()
                                .setModuleID(key)
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value))
                                .setUnitID(unitID));
                        break;
                    case TempSensorDS18B20.REFERENCE:
                        modules.add(new TempSensorDS18B20()
                                .setModuleID(key)
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value))
                                .setUnitID(unitID));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unrecognized module reference: %s", module_ref));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Malformed module reference in payload");
            }
        }
        return modules;
    }

    private Map<String, String> filterPayload(Map<String, String> payload) {
        Map<String, String> payloadFiltered = new HashMap<>();
        for (String key : payload.keySet()) {
            if (key.contains("|")) {
                payloadFiltered.put(key, payload.get(key));
            }
        }
        return payloadFiltered;
    }

    private void handleUnitError(Message message) throws IllegalArgumentException {
        if (message.getPayload().get("error") == null) {
            throw new IllegalArgumentException("Missing error message in unit error payload");
        }
        UnitError unitError = new UnitError()
                .setId(message.getPayload().get("unitID"))
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setArrived(message.getTimeArrived())
                .setError(message.getPayload().get("error"));
        errorRepository.save(unitError);
        log.info("Unit error message: {}", unitError);
    }

    private void sendUnitToReporting(Unit unit) {
        unitUpdateReporter.sendUnitToReporting(unit);
    }


}
