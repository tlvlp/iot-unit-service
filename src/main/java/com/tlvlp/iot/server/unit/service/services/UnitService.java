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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


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

    Unit saveUnit(Unit unit) {
        return repository.save(unit);
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
        var unitSaved = repository.save(newUnit);
        log.info("Added new unit: {}", unitSaved);
        return unitSaved;
    }

    private String getUnitControlTopic(String unitID) {
        return String.format("/units/%s/control", unitID);
    }

    Unit updateUnitFromMessage(Unit unitUpdate, Message message) {
        Set<Module> originalModules = unitUpdate.getModules();
        unitUpdate
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setActive(true)
                .setLastSeen(LocalDateTime.now())
                .setModules(moduleService.parseModulesFromPayload(message.getPayload(), unitUpdate.getUnitID()));
        var unitSaved = repository.save(unitUpdate);
        logModuleChanges(originalModules, unitSaved.getModules());
        log.info("Updated unit: {}", unitSaved);
        return unitSaved;
    }

    private void logModuleChanges(Set<Module> originalModules, Set<Module> newModules) {
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

    public Unit modifyUnitScheduledEventList(String unitID, String eventID, Boolean isDeletion) throws IllegalArgumentException {
        if (!isValidString(unitID) || !isValidString(eventID)) {
            throw new IllegalArgumentException("Invalid request body!");
        }
        Optional<Unit> unitDB = repository.findById(unitID);
        unitDB.orElseThrow(() ->
                new IllegalArgumentException(String.format(
                        "Scheduled event cannot be removed! Unit does not exist: %s", unitID)));
        Unit unit = unitDB.get();
        Set<String> unitEvents = unit.getScheduledEvents();
        if (isDeletion) {
            unitEvents.remove(eventID);
            log.info("Removed scheduled event from unit: unitID:{} eventID:{}", unitID, eventID);
        } else {
            unitEvents.add(eventID);
            log.info("Added scheduled event to unit: unitID:{} eventID:{}", unitID, eventID);
        }
        unit.setScheduledEvents(unitEvents);
        return repository.save(unit);
    }

    private Boolean isValidString(String str) {
        return str != null && !str.isEmpty();
    }
}
