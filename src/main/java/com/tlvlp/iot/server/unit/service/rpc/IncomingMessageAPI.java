package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.services.IncomingMessageService;
import com.tlvlp.iot.server.unit.service.persistence.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomingMessageAPI {

    private IncomingMessageService incomingMessageService;

    public IncomingMessageAPI(IncomingMessageService incomingMessageService) {
        this.incomingMessageService = incomingMessageService;
    }

    @PostMapping("${UNIT_SERVICE_API_INCOMING_MESSAGE}")
    public ResponseEntity postMessage(@RequestBody Message message) {
        return incomingMessageService.handleIncomingMessage(message);
    }
}
