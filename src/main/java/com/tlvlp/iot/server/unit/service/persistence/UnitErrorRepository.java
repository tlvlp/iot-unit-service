package com.tlvlp.iot.server.unit.service.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface UnitErrorRepository
        extends MongoRepository<UnitError, String>, QueryByExampleExecutor<UnitError> {

}
