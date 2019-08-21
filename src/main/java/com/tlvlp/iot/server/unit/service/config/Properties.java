package com.tlvlp.iot.server.unit.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MQTT_CLIENT_MESSAGE_CONTROL_URI}")
    public String MQTT_CLIENT_MESSAGE_CONTROL_URI;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS}")
    public String MCU_MQTT_TOPIC_GLOBAL_STATUS;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST}")
    public String MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_INACTIVE}")
    public String MCU_MQTT_TOPIC_GLOBAL_INACTIVE;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_ERROR}")
    public String MCU_MQTT_TOPIC_GLOBAL_ERROR;


}
