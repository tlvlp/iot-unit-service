package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Message;
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

    public ResponseEntity<String> sendMessage(Message message) throws MessageFrowardingException {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    properties.MQTT_CLIENT_API_OUTGOING_MESSAGE_URL,
                    message,
                    String.class);
            log.info("Message sent to the MQTT Client Service: {}", message);
            return response;
        } catch (ResourceAccessException e) {
            String err = String.format("MQTT Client Service is not responding: %s", e.getMessage());
            log.error(err);
            throw new MessageFrowardingException(err);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            String err = String.format("Cannot send message to MQTT Client Service: %s", e.getResponseBodyAsString());
            log.error(err);
            throw new MessageFrowardingException(err);
        }
    }

}
