package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.modules.Relay;
import com.tlvlp.iot.server.unit.service.services.OutgoingMessageService;
import com.tlvlp.iot.server.unit.service.services.Unit;
import com.tlvlp.iot.server.unit.service.services.UnitDBService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UnitControl {

    private UnitDBService unitDBService;
    private OutgoingMessageService outgoingMessageService;

    public UnitControl(UnitDBService unitDBService, OutgoingMessageService outgoingMessageService) {
        this.unitDBService = unitDBService;
        this.outgoingMessageService = outgoingMessageService;
    }

    @GetMapping("${UNIT_SERVICE_UNIT_LIST_ALL_CONTROL}")
    public ResponseEntity<List<Unit>> getAllUnits() {
        return new ResponseEntity<List<Unit>>(unitDBService.getAllUnits(), HttpStatus.ACCEPTED);
    }

    @GetMapping("${UNIT_SERVICE_UNIT_LIST_BY_EXAMPLE_CONTROL}")
    public ResponseEntity<List<Unit>> getUnitsByExample(@RequestBody Unit exampleUnit) {
        return new ResponseEntity<List<Unit>>(unitDBService.getUnitsByExample(exampleUnit), HttpStatus.ACCEPTED);
    }

    @PostMapping("${UNIT_SERVICE_UNIT_REQUEST_GLOBAL_STATUS_CONTROL}")
    public ResponseEntity<String> sendGlobalStatusRequest() {
        try {
            return outgoingMessageService.sendGlobalStatusRequest();
        } catch (MessageFrowardingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("${UNIT_SERVICE_RELAY_CONTROL}")
    public ResponseEntity<String> sendRelayControlToUnit(@RequestParam String unitID,
                                                         @RequestParam String relayID,
                                                         @RequestParam Relay.State newState) {
        try {
            return outgoingMessageService.sendRelayControlToUnit(unitID, relayID, newState);
        } catch (MessageFrowardingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
