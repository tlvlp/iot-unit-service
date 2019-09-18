package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import com.tlvlp.iot.server.unit.service.rpc.MessageFrowardingException;
import com.tlvlp.iot.server.unit.service.rpc.OutgoingMessageSender;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OutgoingMessageComposer {

    private Properties properties;
    private OutgoingMessageSender messageForwarder;
    private UnitRepository unitRepository;

    public OutgoingMessageComposer(Properties properties, OutgoingMessageSender messageForwarder,
                                   UnitRepository unitRepository) {
        this.properties = properties;
        this.messageForwarder = messageForwarder;
        this.unitRepository = unitRepository;
    }

    public ResponseEntity<String> composeGlobalStatusRequest() throws MessageFrowardingException {
        Message message = new Message()
                .setTopic(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST())
                .setPayload(new HashMap<>());
        return messageForwarder.sendMessage(message);
    }

    public ResponseEntity<String> composeModuleControlMessage(Module module)
            throws MessageFrowardingException, IllegalArgumentException {
        Unit unit = getUnitIfModuleIsValid(module);
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put(module.getModuleID(), module.getValue().toString());
        Message message = new Message()
                .setTopic(unit.getControlTopic())
                .setPayload(payloadMap);
        return messageForwarder.sendMessage(message);
    }

    private Unit getUnitIfModuleIsValid(Module module) throws IllegalArgumentException {
        String unitID = module.getUnitID();
        Optional<Unit> unitDB = unitRepository.findById(unitID);
        unitDB.orElseThrow(() -> new IllegalArgumentException(
                String.format("Cannot send module control message: unitID is not in the database: %s", unitID)));
        Unit unit = unitDB.get();
        boolean isModuleInUnit =
                unit.getModules().stream().anyMatch(m -> m.getModuleID().equals(module.getModuleID()));
        if(!isModuleInUnit) {
            throw new IllegalArgumentException(
                    String.format("Cannot send module control message: " +
                                    "module is not present in unit: moduleID:%s unitID:%s",
                            module.getModuleID(), unitID));
        }
        return unit;
    }

}
