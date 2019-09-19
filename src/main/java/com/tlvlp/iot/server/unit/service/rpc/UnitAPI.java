package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import com.tlvlp.iot.server.unit.service.services.OutgoingMessageComposer;
import com.tlvlp.iot.server.unit.service.services.UnitLogService;
import com.tlvlp.iot.server.unit.service.services.UnitService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UnitAPI {

    private UnitService unitService;
    private UnitLogService unitLogService;
    private OutgoingMessageComposer outgoingMessageComposer;

    public UnitAPI(UnitService unitService, UnitLogService unitLogService,
                   OutgoingMessageComposer outgoingMessageComposer) {
        this.unitService = unitService;
        this.unitLogService = unitLogService;
        this.outgoingMessageComposer = outgoingMessageComposer;
    }

    @GetMapping("${UNIT_SERVICE_API_LIST_ALL_UNIT}")
    public ResponseEntity<List<Unit>> getAllUnits() {
        return new ResponseEntity<>(unitService.getAllUnits(), HttpStatus.OK);
    }

    @GetMapping("${UNIT_SERVICE_API_LIST_UNITS_BY_EXAMPLE}")
    public ResponseEntity<List<Unit>> getUnitsByExample(@RequestBody Unit exampleUnit) {
        return new ResponseEntity<>(unitService.getUnitsByExample(exampleUnit), HttpStatus.OK);
    }

    @PostMapping("${UNIT_SERVICE_API_REQUEST_GLOBAL_STATUS}")
    public ResponseEntity<String> sendGlobalStatusRequest() {
        try {
            return outgoingMessageComposer.composeGlobalStatusRequest();
        } catch (MessageFrowardingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("${UNIT_SERVICE_API_ADD_SCHEDULED_EVENT}")
    public ResponseEntity<Unit> addScheduledEventToUnit(@RequestParam String unitID,
                                                        @RequestParam String eventID) {
        try {
            return new ResponseEntity<>(
                    unitService.modifyUnitScheduledEventList(unitID, eventID, false),
                    HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("${UNIT_SERVICE_API_DELETE_SCHEDULED_EVENT}")
    public ResponseEntity<Unit> deleteScheduledEventFromUnit(@RequestParam String unitID,
                                                             @RequestParam String eventID) {
        try {
            return new ResponseEntity<>(
                    unitService.modifyUnitScheduledEventList(unitID, eventID, true),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("${UNIT_SERVICE_API_MODULE_CONTROL}")
    public ResponseEntity<String> getModuleControlMessage(@RequestBody Module module) {
        try {
            return outgoingMessageComposer.composeModuleControlMessage(module);
        } catch (MessageFrowardingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("${UNIT_SERVICE_API_GET_UNIT_LOGS}")
    public ResponseEntity<List<UnitLog>> getUnitLogs(@RequestParam String unitID,
                                                     @RequestParam
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeFrom,
                                                     @RequestParam
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeTo) {
        return new ResponseEntity<>(unitLogService.getUnitLogs(unitID, timeFrom, timeTo), HttpStatus.OK);
    }
}
