package com.tlvlp.iot.server.unit.service.persistence;

import com.tlvlp.iot.server.unit.service.services.Unit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface UnitRepository
        extends MongoRepository<Unit, String>, QueryByExampleExecutor<Unit> {

}
