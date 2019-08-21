package com.tlvlp.iot.server.unit.service.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UnitDBService {


    public ResponseEntity<Set<Unit>> getAllUnits() {
        //TODO return response regarding db results
    }

    public ResponseEntity<Set<Unit>> getUnitsByExample(Unit exampleUnit) {
        //TODO return response regarding db results
    }
}
