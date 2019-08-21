package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.services.OutgoingMessageService;
import com.tlvlp.iot.server.unit.service.services.Unit;
import com.tlvlp.iot.server.unit.service.services.UnitDBService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
public class UnitControl {

    private UnitDBService unitDBService;
    private OutgoingMessageService outgoingMessageService;
    //TODO constructor

    @GetMapping("${UNIT_SERVICE_UNIT_LIST_ALL_CONTROL}")
    public ResponseEntity<Set<Unit>> getAllUnits() {
        return unitDBService.getAllUnits();
    }

    @GetMapping("${UNIT_SERVICE_UNIT_LIST_BY_EXAMPLE_CONTROL}")
    public ResponseEntity<Set<Unit>> getUnitsByExample(@RequestBody Unit exampleUnit) {
        return unitDBService.getUnitsByExample(exampleUnit);
    }

    @GetMapping("${UNIT_SERVICE_UNIT_REQUEST_GLOBAL_STATUS_CONTROL}")
    public ResponseEntity<String> sendGlobalStatusRequest() {
        return outgoingMessageService.sendGlobalStatusRequest();
    }

    @GetMapping("${UNIT_SERVICE_UNIT_REQUEST_UNIT_CONTROL}") //TODO WRONG CONCEPT - implement module controls
    public ResponseEntity<String> sendControlMessageToUnit(@RequestBody Map<String, Object> ) {
        return outgoingMessageService.sendControlMessageToUnit();
    }
}
