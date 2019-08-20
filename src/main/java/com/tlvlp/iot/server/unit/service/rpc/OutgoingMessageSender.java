package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.services.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class OutgoingMessageSender {

    private static final Logger log = LoggerFactory.getLogger(OutgoingMessageSender.class);
    private RestTemplate restTemplate;
    private Properties properties;

    public OutgoingMessageSender(RestTemplate restTemplate, Properties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void forwardMessage(Message message) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    properties.MQTT_CLIENT_MESSAGE_RESOURCE_URI,
                    message,
                    String.class);
            log.info("Message forwarded to the MQTT Client Service: {}", message);
        } catch (ResourceAccessException e) {
            log.error("MQTT Client Service is not responding: {}", e.getMessage());
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Cannot forward message to MQTT Client Service: {}", e.getResponseBodyAsString());
        }
    }


}
