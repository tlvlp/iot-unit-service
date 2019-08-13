package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomingMessageResource {

    private Properties properties;

    public IncomingMessageResource(Properties properties) {
        this.properties = properties;
    }

    @PostMapping("${UNIT_SERVICE_MESSAGE_RESOURCE}")
    public ResponseEntity postMessage() {
        // TODO: process incoming message
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
