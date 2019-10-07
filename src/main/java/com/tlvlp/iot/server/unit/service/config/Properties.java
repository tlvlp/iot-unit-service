package com.tlvlp.iot.server.unit.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS}")
    private String MCU_MQTT_TOPIC_GLOBAL_STATUS;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST}")
    private String MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_INACTIVE}")
    private String MCU_MQTT_TOPIC_GLOBAL_INACTIVE;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_ERROR}")
    private String MCU_MQTT_TOPIC_GLOBAL_ERROR;


    public String getMCU_MQTT_TOPIC_GLOBAL_STATUS() {
        return MCU_MQTT_TOPIC_GLOBAL_STATUS;
    }

    public String getMCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST() {
        return MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST;
    }

    public String getMCU_MQTT_TOPIC_GLOBAL_INACTIVE() {
        return MCU_MQTT_TOPIC_GLOBAL_INACTIVE;
    }

    public String getMCU_MQTT_TOPIC_GLOBAL_ERROR() {
        return MCU_MQTT_TOPIC_GLOBAL_ERROR;
    }
}
