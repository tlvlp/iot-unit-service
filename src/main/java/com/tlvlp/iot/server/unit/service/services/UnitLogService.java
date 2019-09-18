package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
class UnitLogService {

    UnitLog getUnitLogInactiveFromMessage(Message message) {
        return getUnitLogBase(message)
                .setLogEntry("Unit became inactive");
    }

    UnitLog getUnitLogErrorFromMessage(Message message) {
        return getUnitLogBase(message)
                .setLogEntry(message.getPayload().get("error"));
    }

    private UnitLog getUnitLogBase(Message message) {
        return new UnitLog()
                .setLogID(getLogID())
                .setUnitID(message.getPayload().get("unitID"))
                .setProject(message.getPayload().get("project"))
                .setName(message.getPayload().get("name"))
                .setArrived(LocalDateTime.now());
    }

    private String getLogID() {
        return String.format("%s-LOG-%S", LocalDate.now().toString(), UUID.randomUUID().toString());
    }

}
