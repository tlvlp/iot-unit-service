package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.modules.Relay;
import com.tlvlp.iot.server.unit.service.services.OutgoingMessageService;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.services.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class UnitAPI {

    private UnitService unitService;
    private OutgoingMessageService outgoingMessageService;

    public UnitAPI(UnitService unitService, OutgoingMessageService outgoingMessageService) {
        this.unitService = unitService;
        this.outgoingMessageService = outgoingMessageService;
    }

    @GetMapping("${UNIT_SERVICE_API_LIST_ALL_UNIT}")
    public ResponseEntity<List<Unit>> getAllUnits() {
        return new ResponseEntity<List<Unit>>(unitService.getAllUnits(), HttpStatus.ACCEPTED);
    }

    @GetMapping("${UNIT_SERVICE_API_LIST_UNITS_BY_EXAMPLE}")
    public ResponseEntity<List<Unit>> getUnitsByExample(@RequestBody Unit exampleUnit) {
        return new ResponseEntity<List<Unit>>(unitService.getUnitsByExample(exampleUnit), HttpStatus.ACCEPTED);
    }

    @PostMapping("${UNIT_SERVICE_API_REQUEST_GLOBAL_STATUS}")
    public ResponseEntity<String> sendGlobalStatusRequest() {
        try {
            return outgoingMessageService.sendGlobalStatusRequest();
        } catch (MessageFrowardingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("${UNIT_SERVICE_API_ADD_SCHEDULED_EVENT}")
    public ResponseEntity<Unit> addScheduledEventToUnit(@RequestBody Map<String, String> requestBody) {
        try {
            return new ResponseEntity<>(unitService.addScheduledEventToUnit(requestBody), HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("${UNIT_SERVICE_API_DELETE_SCHEDULED_EVENT}")
    public ResponseEntity<Unit> deleteScheduledEvent(@RequestBody Map<String, String> requestBody) {
        try {
            return new ResponseEntity<>(unitService.deleteScheduledEventFromUnit(requestBody), HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("${UNIT_SERVICE_API_RELAY_CONTROL}")
    public ResponseEntity<String> handleRelayControl(@RequestBody Relay relay) {
        try {
            return outgoingMessageService.sendRelayControlToUnit(relay);
        } catch (MessageFrowardingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
