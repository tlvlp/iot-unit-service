package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import com.tlvlp.iot.server.unit.service.services.IncomingMessageHandler;
import com.tlvlp.iot.server.unit.service.services.OutgoingMessageComposer;
import com.tlvlp.iot.server.unit.service.services.UnitLogService;
import com.tlvlp.iot.server.unit.service.services.UnitService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UnitAPI {

    private UnitService unitService;
    private UnitLogService unitLogService;
    private OutgoingMessageComposer outgoingMessageComposer;
    private IncomingMessageHandler incomingMessageHandler;

    public UnitAPI(UnitService unitService, UnitLogService unitLogService, OutgoingMessageComposer outgoingMessageComposer, IncomingMessageHandler incomingMessageHandler) {
        this.unitService = unitService;
        this.unitLogService = unitLogService;
        this.outgoingMessageComposer = outgoingMessageComposer;
        this.incomingMessageHandler = incomingMessageHandler;
    }

    @PostMapping("${UNIT_SERVICE_API_INCOMING_MESSAGE}")
    public ResponseEntity<HashMap<String, Object>> handleIncomingMessage(@RequestBody @Valid Message message) {
        try {
            return new ResponseEntity<>(
                    incomingMessageHandler.handleIncomingMessage(message),
                    HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("${UNIT_SERVICE_API_LIST_ALL_UNIT}")
    public ResponseEntity<List<Unit>> getAllUnits() {
        return new ResponseEntity<>(unitService.getAllUnits(), HttpStatus.OK);
    }

    @GetMapping("${UNIT_SERVICE_API_GET_UNIT_BY_ID}")
    public ResponseEntity<Unit> getUnitByID(@RequestParam @NotBlank String unitID) {
        var unitOptional = unitService.getUnitByID(unitID);
        return unitOptional.map(unit -> new ResponseEntity<>(unit, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("${UNIT_SERVICE_API_ADD_SCHEDULED_EVENT}")
    public ResponseEntity<Unit> addScheduledEventToUnit(@RequestBody @NotEmpty Map<String, String> requestDetails) {
        try {
            return new ResponseEntity<>(
                    unitService.modifyUnitScheduledEventList(requestDetails, false),
                    HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("${UNIT_SERVICE_API_DELETE_SCHEDULED_EVENT}")
    public ResponseEntity<Unit> deleteScheduledEventFromUnit(@RequestBody @NotEmpty Map<String, String> requestDetails) {
        try {
            return new ResponseEntity<>(
                    unitService.modifyUnitScheduledEventList(requestDetails, true),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("${UNIT_SERVICE_API_REQUEST_GLOBAL_STATUS}")
    public ResponseEntity<Message> getGlobalStatusRequest() {
        return new ResponseEntity<>(outgoingMessageComposer.composeGlobalStatusRequest(), HttpStatus.OK);
    }

    @PostMapping("${UNIT_SERVICE_API_MODULE_CONTROL}")
    public ResponseEntity<Message> getModuleControlMessage(@RequestBody @Valid Module module) {
        try {
            return new ResponseEntity<>(
                    outgoingMessageComposer.composeModuleControlMessage(module),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("${UNIT_SERVICE_API_GET_UNIT_LOGS}")
    public ResponseEntity<List<UnitLog>> getUnitLogs(@RequestParam @NotBlank String unitID,
                                                     @RequestParam
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeFrom,
                                                     @RequestParam
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeTo) {
        return new ResponseEntity<>(unitLogService.getUnitLogs(unitID, timeFrom, timeTo), HttpStatus.OK);
    }
}
