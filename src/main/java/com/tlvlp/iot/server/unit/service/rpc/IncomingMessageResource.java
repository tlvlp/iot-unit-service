package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomingMessageResource {

    private MessageService messageService;

    public IncomingMessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("${UNIT_SERVICE_MESSAGE_RESOURCE}")
    public ResponseEntity postMessage(@RequestBody Message message) {
        return messageService.handleIncomingMessage(message);
    }
}
