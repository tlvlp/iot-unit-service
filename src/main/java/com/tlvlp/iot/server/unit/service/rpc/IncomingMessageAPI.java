package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.services.IncomingMessageHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomingMessageAPI {

    private IncomingMessageHandler incomingMessageHandler;

    public IncomingMessageAPI(IncomingMessageHandler incomingMessageHandler) {
        this.incomingMessageHandler = incomingMessageHandler;
    }

    @PostMapping("${UNIT_SERVICE_API_INCOMING_MESSAGE}")
    public ResponseEntity handleIncomingMessage(@RequestBody Message message) {
        return incomingMessageHandler.handleIncomingMessage(message);
    }
}
