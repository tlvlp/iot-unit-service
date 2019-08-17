package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.modules.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitError;
import com.tlvlp.iot.server.unit.service.persistence.UnitErrorRepository;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
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

    private Properties properties;
    private UnitRepository repository;
    private UnitErrorRepository errorRepository;

    public MessageService(Properties properties, UnitRepository repository, UnitErrorRepository errorRepository) {
        this.properties = properties;
        this.repository = repository;
        this.errorRepository = errorRepository;
    }

    ResponseEntity handleIncomingMessage(Message message) {
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
                updateUnit(unitDB.get());
            } else {
                createUnit(message);
            }

        }
        return new ResponseEntity<String>("ok", HttpStatus.ACCEPTED);
    }

    private void saveUnitError(Message message) {
        UnitError unitError = new UnitError()
                .setId(message.getUnitID())
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setArrived(message.getTimeArrived())
                .setError(message.getPayload().get("error"));
        errorRepository.save(unitError);
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
    }

    private Set<Module> getModulesFromPayload(Map<String, String> payload) {
        Set<Module> modules = new HashSet<>();
        // TODO parse payload to modules
        return  modules;
    }

    private void updateUnit(Unit unit) {
        // TODO
        // handle disappearing modules (remove from the list)
    }

    private void inactivateUnit(Unit unit) {
        unit.setActive(false);
        repository.save(unit);
    }

}
