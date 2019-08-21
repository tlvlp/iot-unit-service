package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.modules.Relay;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import com.tlvlp.iot.server.unit.service.rpc.MessageFrowardingException;
import com.tlvlp.iot.server.unit.service.rpc.OutgoingMessageForwarder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OutgoingMessageService {

    private static final Logger log = LoggerFactory.getLogger(OutgoingMessageService.class);
    private Properties properties;
    private OutgoingMessageForwarder messageForwarder;
    private UnitRepository unitRepository;

    public OutgoingMessageService(Properties properties, OutgoingMessageForwarder messageForwarder,
                                  UnitRepository unitRepository) {
        this.properties = properties;
        this.messageForwarder = messageForwarder;
        this.unitRepository = unitRepository;
    }

    public ResponseEntity<String> sendRelayControlToUnit(String unitID, String relayID, Relay.State state)
            throws MessageFrowardingException {
        Optional<Unit> unitDB = unitRepository.findById(unitID);
        if (!unitDB.isPresent()) {
            String err = String.format("The requested unit is not found: %s", unitID);
            log.error(err);
            throw new MessageFrowardingException(err);
        }
        Unit unit = unitDB.get();
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put(relayID, state.toString());
       Message newMessage = new Message()
               .setTimeArrived(LocalDateTime.now())
               .setDirection(Message.Direction.OUTGOING)
               .setTopic(unit.getControlTopic())
               .setUnitID(unit.getId())
               .setPayload(payloadMap);
       return messageForwarder.forwardMessage(newMessage);
    }

    public ResponseEntity<String> sendGlobalStatusRequest() throws MessageFrowardingException {
        Message newMessage = new Message()
                .setTimeArrived(LocalDateTime.now())
                .setDirection(Message.Direction.OUTGOING)
                .setTopic(properties.MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST)
                .setUnitID("global")
                .setPayload(new HashMap<String, String>());
        return messageForwarder.forwardMessage(newMessage);
    }

}
