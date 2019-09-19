package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import com.tlvlp.iot.server.unit.service.persistence.UnitLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UnitLogService {

    private UnitLogRepository repository;
    private MongoTemplate mongoTemplate;

    public UnitLogService(UnitLogRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public UnitLog saveUnitLogInactiveFromMessage(Message message) {
        UnitLog log = getUnitLogBase(message)
                .setLogEntry("Unit became inactive");
        return repository.save(log);
    }

    public UnitLog saveUnitLogErrorFromMessage(Message message) {
        UnitLog log = getUnitLogBase(message)
                .setLogEntry(message.getPayload().get("logEntry"));
        return repository.save(log);
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

    public List<UnitLog> getUnitLogs(String unitID, LocalDateTime timeFrom, LocalDateTime timeTo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("unitID").is(unitID));
        query.addCriteria(Criteria.where("arrived").gte(timeFrom).lt(timeTo));
        query.with(Sort.by(Sort.Direction.ASC, "arrived"));
        query.fields()
                .include("arrived")
                .include("logEntry");
        return mongoTemplate.find(query, UnitLog.class);
    }
}
