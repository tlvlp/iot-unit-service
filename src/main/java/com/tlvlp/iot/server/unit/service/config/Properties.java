package com.tlvlp.iot.server.unit.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MQTT_CLIENT_MESSAGE_RESOURCE_URI}")
    public String MQTT_CLIENT_MESSAGE_RESOURCE_URI;

}
