package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class UnitService {

    private static final Logger log = LoggerFactory.getLogger(UnitService.class);
    private UnitRepository repository;
    private ModuleService moduleService;

    public UnitService(UnitRepository repository, ModuleService moduleService) {
        this.repository = repository;
        this.moduleService = moduleService;
    }

    public List<Unit> getAllUnits() {
        return repository.findAll();
    }

    public List<Unit> getUnitsByExample(Unit exampleUnit) {
        return repository.findAll(Example.of(exampleUnit, ExampleMatcher.matching().withIgnoreNullValues()));
    }

    public Optional<Unit> getUnitByID(String unitID) {
        return repository.findById(unitID);
    }

    void saveUnit(Unit unit) {
        repository.save(unit);
    }

    Unit createUnitFromMessage(Message message) {
        String unitID = message.getPayload().get("unitID");
        Unit newUnit = new Unit()
                .setUnitID(unitID)
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setControlTopic(getUnitControlTopic(unitID))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(moduleService.parseModulesFromPayload(message.getPayload(), unitID));
        repository.save(newUnit);
        log.info("Added new unit: {}", newUnit);
        return newUnit;
    }

    private String getUnitControlTopic(String unitID) {
        return String.format("/units/%s/control", unitID);
    }

    Unit updateUnitFromMessage(Unit unit, Message message) {
        Set<Module> originalModules = unit.getModules();
        unit
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(moduleService.parseModulesFromPayload(message.getPayload(), unit.getUnitID()));
        repository.save(unit);
        logModuleChanges(unit.getUnitID(), originalModules, unit.getModules());
        log.info("Updated unit: {}", unit);
        return unit;
    }

    private void logModuleChanges(String unitID, Set<Module> originalModules, Set<Module> newModules) {
        Set<Module> addedModules = new HashSet<>(newModules);
        addedModules.removeAll(originalModules);
        if (!addedModules.isEmpty()) {
            log.info("New modules have been added: {}", addedModules);
        }
        Set<Module> removedModules = new HashSet<>(originalModules);
        removedModules.removeAll(newModules);
        if (!removedModules.isEmpty()) {
            log.warn("Modules have been removed: {}", removedModules);
        }
    }

    public Unit addScheduledEventToUnit(Map<String, String> requestBody) {
        return handleEventChange(requestBody, false);
    }

    public Unit deleteScheduledEventFromUnit(Map<String, String> requestBody) {
        return handleEventChange(requestBody, true);
    }

    private Unit handleEventChange(Map<String, String> requestBody, Boolean isDeletion) {
        String unitID = requestBody.get("unitID");
        String eventID = requestBody.get("eventID");
        if (!isValidString(unitID) || !isValidString(eventID)) {
            throw new IllegalArgumentException("Invalid request body!");
        }
        Optional<Unit> unitDB = repository.findById(unitID);
        unitDB.orElseThrow(() ->
                new IllegalArgumentException("Scheduled event cannot be removed! Unit does not exist: " + unitID));
        Unit unit = unitDB.get();
        Set<String> unitEvents = unit.getScheduledEvents();
        if (isDeletion) {
            unitEvents.remove(eventID);
            log.info("Removed scheduled event from unit: unitID:{} eventID:{}", unitID, eventID);
        } else {
            unitEvents.add(eventID);
            log.info("Added scheduled event to unit: unitID:{} eventID:{}", unitID, eventID);
        }
        repository.save(unit);
        return unit;
    }

    private Boolean isValidString(String str) {
        return str != null && !str.isEmpty();
    }
}
