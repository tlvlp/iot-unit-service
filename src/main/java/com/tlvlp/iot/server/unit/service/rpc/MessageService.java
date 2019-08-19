package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.modules.*;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitError;
import com.tlvlp.iot.server.unit.service.persistence.UnitErrorRepository;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private Properties properties;
    private UnitRepository repository;
    private UnitErrorRepository errorRepository;

    public MessageService(Properties properties, UnitRepository repository, UnitErrorRepository errorRepository) {
        this.properties = properties;
        this.repository = repository;
        this.errorRepository = errorRepository;
    }

    ResponseEntity handleIncomingMessage(Message message) {
        try {
            checkMessageValidity(message);
            String topic = message.getTopic();
            if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_ERROR)) {
                handleUnitError(message);
            } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_INACTIVE)) {
                handleInactiveUnit(message);
            } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS)) {
                handleUnitStatus(message);
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

    private void handleUnitError(Message message) throws IllegalArgumentException{
        if (message.getPayload().get("error") == null) {
            throw new IllegalArgumentException("Missing error message in unit error payload");
        }
        UnitError unitError = new UnitError()
                .setId(message.getUnitID())
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setArrived(message.getTimeArrived())
                .setError(message.getPayload().get("error"));
        errorRepository.save(unitError);
        log.info(String.format("Unit error message: %s", unitError));
    }

    private void handleInactiveUnit(Message message) {
        Optional<Unit> unitDBOpt = repository.findById(message.getUnitID());
        if (unitDBOpt.isPresent()) {
            Unit unitDB = unitDBOpt.get();
            unitDB.setActive(false);
            repository.save(unitDB);
            log.info(String.format("Unit is inactive: UnitID:%s Project:%s Name:%s",
                    unitDB.getId(), unitDB.getProject(), unitDB.getName() ));
        }

    }

    private void  handleUnitStatus(Message message) {
        Optional<Unit> unitDB = repository.findById(message.getUnitID());
        if (unitDB.isPresent()) {
            updateUnit(unitDB.get(), message);
        } else {
            createUnit(message);
        }
    }

    private void createUnit(Message message) {
        Unit newUnit = new Unit()
                .setId(message.getUnitID())
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(getModulesFromPayload(message.getPayload()));
        repository.save(newUnit);
        log.info(String.format("Added new unit: %s", newUnit));
    }

    private void updateUnit(Unit unit, Message message) {
        Set<Module> originalModules = unit.getModules();
        unit
                .setId(message.getUnitID())
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(getModulesFromPayload(message.getPayload()));
        repository.save(unit);
        logAddedModules(unit.getId(), originalModules, unit.getModules());
        logRemovedModules(unit.getId(), originalModules, unit.getModules());
        log.info(String.format("Updated unit: %s", unit));
    }

    private void logAddedModules(String unitID, Set<Module> originalModules, Set<Module> newModules) {
        newModules.removeAll(originalModules);
        if (!newModules.isEmpty()) {
            log.info(String.format("New modules have been added! UnitID:%s Modules:%s", unitID, newModules));
        }
    }

    private void logRemovedModules(String unitID, Set<Module> originalModules, Set<Module> newModules) {
        originalModules.removeAll(newModules);
        if (!originalModules.isEmpty()) {
            log.warn(String.format("Modules have been removed! UnitID:%s Modules:%s", unitID, originalModules));
        }
    }

    private Set<Module> getModulesFromPayload(Map<String, String> payload) throws IllegalArgumentException{
        Set<Module> modules = new HashSet<>();
        // Remove non-module payload elements
        payload.remove("id");
        payload.remove("project");
        payload.remove("name");
        // Parse modules
        for (String key : payload.keySet()) {
            try {
                String module_ref = key.split("\\|")[0];
                String module_name = key.split("\\|")[1];
                String module_value = payload.get(key);
                switch (module_ref) {
                    case Relay.REFERENCE:
                        modules.add(new Relay()
                                .setName(module_name)
                                .setState(module_value.equals("on") ? Relay.State.on : Relay.State.off));
                        break;
                    case LightSensorGl5528.REFERENCE:
                        modules.add(new LightSensorGl5528()
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value)));
                        break;
                    case SoilMoistureSensor.REFERENCE:
                        modules.add(new SoilMoistureSensor()
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value)));
                        break;
                    case TempSensorDS18B20.REFERENCE:
                        modules.add(new TempSensorDS18B20()
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value)));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unrecognized module reference: %s", module_ref));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Malformed module reference in payload");
            }
        }
        return  modules;
    }

}
