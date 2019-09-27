package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@Service
public class OutgoingMessageComposer {

    private Properties properties;
    private UnitRepository unitRepository;

    public OutgoingMessageComposer(Properties properties, UnitRepository unitRepository) {
        this.properties = properties;
        this.unitRepository = unitRepository;
    }

    public Message composeGlobalStatusRequest() {
        return new Message()
                .setTopic(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST())
                .setPayload(new HashMap<>());
    }

    public Message composeModuleControlMessage(Module module) throws MessageProcessingException {
        Unit unit = getUnitIfModuleIsValid(module);
        return new Message()
                .setTopic(unit.getControlTopic())
                .setPayload(Collections.singletonMap(
                        module.getModuleID(),
                        module.getValue().toString()));

    }

    private Unit getUnitIfModuleIsValid(Module module) throws MessageProcessingException {
        String unitID = module.getUnitID();
        Optional<Unit> unitDB = unitRepository.findById(unitID);
        unitDB.orElseThrow(() -> new MessageProcessingException(
                String.format("Cannot send module control message: unitID is not in the database: %s", unitID)));
        Unit unit = unitDB.get();
        var isModuleInUnit =
                unit.getModules().stream()
                        .anyMatch(m -> m.getModuleID().equals(module.getModuleID()));
        if(!isModuleInUnit) {
            throw new MessageProcessingException(
                    String.format("Cannot send module control message: " +
                                    "module is not present in unit: moduleID:%s unitID:%s",
                            module.getModuleID(), unitID));
        }
        return unit;
    }

}
