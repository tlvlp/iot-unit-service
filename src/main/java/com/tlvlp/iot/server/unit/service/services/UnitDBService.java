package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UnitDBService {

    private UnitRepository repository;

    public UnitDBService(UnitRepository repository) {
        this.repository = repository;
    }

    public List<Unit> getAllUnits() {
        return repository.findAll();
    }

    public List<Unit> getUnitsByExample(Unit exampleUnit) {
        return repository.findAll(Example.of(exampleUnit));
    }
}
