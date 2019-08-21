package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.services.IncomingMessageService;
import com.tlvlp.iot.server.unit.service.services.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomingMessageControl {

    private IncomingMessageService incomingMessageService;

    public IncomingMessageControl(IncomingMessageService incomingMessageService) {
        this.incomingMessageService = incomingMessageService;
    }

    @PostMapping("${UNIT_SERVICE_MESSAGE_CONTROL}")
    public ResponseEntity postMessage(@RequestBody Message message) {
        return incomingMessageService.handleIncomingMessage(message);
    }
}
