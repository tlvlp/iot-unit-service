package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.modules.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitError;
import com.tlvlp.iot.server.unit.service.persistence.UnitErrorRepository;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        String errorInMessage = checkMessageValidity(message);
        if(!errorInMessage.isEmpty()) {
            return new ResponseEntity<String>(errorInMessage, HttpStatus.BAD_REQUEST);
        }
        String topic = message.getTopic();
        if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_ERROR)) {
            saveUnitError(message);
        } else if(topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_INACTIVE)) {
            Optional<Unit> unitDB = repository.findById(message.getUnitID());
            if (unitDB.isPresent()) {
                inactivateUnit(unitDB.get());
            }
        } else if (topic.equals(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS)) {
            Optional<Unit> unitDB = repository.findById(message.getUnitID());
            if (unitDB.isPresent()) {
                updateUnit(unitDB.get(), message);
            } else {
                createUnit(message);
            }

        }
        return new ResponseEntity<String>("ok", HttpStatus.ACCEPTED);
    }

    private String checkMessageValidity(Message message) {
        String errorInMessage = "";
        // TODO check if all fields have been included in the message
        return errorInMessage;
    }

    private void saveUnitError(Message message) {
        UnitError unitError = new UnitError()
                .setId(message.getUnitID())
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setArrived(message.getTimeArrived())
                .setError(message.getPayload().get("error"));
        errorRepository.save(unitError);
        log.info(String.format("Unit error message: %s", unitError));
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
        log.info(String.format("Adding new unit: %s", newUnit));
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
        log.info(String.format("Updating unit: %s", unit));
    }

    private void inactivateUnit(Unit unit) {
        unit.setActive(false);
        repository.save(unit);
        log.info(String.format("Inactive unit: UnitID:%s Project:%s Name:%s",
                unit.getId(), unit.getProject(), unit.getName() ));
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

    private Set<Module> getModulesFromPayload(Map<String, String> payload) {
        Set<Module> modules = new HashSet<>();
        // TODO parse payload to modules
        return  modules;
    }

}
