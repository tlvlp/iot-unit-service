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
                    String.format("http://%s:%s%s",
                            properties.getAPI_GATEWAY_NAME(),
                            properties.getAPI_GATEWAY_PORT(),
                            properties.getAPI_GATEWAY_API_OUTGOING_MQTT_MESSAGE()),
                    message,
                    String.class);
            log.info("Message sent: {}", message);
            return response;
        } catch (ResourceAccessException e) {
            String err = String.format("API Gateway is not responding: %s", e.getMessage());
            log.error(err);
            throw new MessageFrowardingException(err);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            String err = String.format("Cannot send message to API Gateway: %s", e.getResponseBodyAsString());
            log.error(err);
            throw new MessageFrowardingException(err);
        }
    }

}
