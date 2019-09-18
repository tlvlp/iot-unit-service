package com.tlvlp.iot.server.unit.service.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface UnitRepository extends MongoRepository<Unit, String>, QueryByExampleExecutor<Unit> {

}
