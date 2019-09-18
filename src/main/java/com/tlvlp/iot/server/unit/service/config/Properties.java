package com.tlvlp.iot.server.unit.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${API_GATEWAY_API_OUTGOING_MQTT_MESSAGE}")
    private String API_GATEWAY_API_OUTGOING_MQTT_MESSAGE;

    @Value("${API_GATEWAY_NAME}")
    private String API_GATEWAY_NAME;

    @Value("${API_GATEWAY_PORT}")
    private String API_GATEWAY_PORT;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS}")
    private String MCU_MQTT_TOPIC_GLOBAL_STATUS;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST}")
    private String MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_INACTIVE}")
    private String MCU_MQTT_TOPIC_GLOBAL_INACTIVE;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_ERROR}")
    private String MCU_MQTT_TOPIC_GLOBAL_ERROR;


    public String getAPI_GATEWAY_API_OUTGOING_MQTT_MESSAGE() {
        return API_GATEWAY_API_OUTGOING_MQTT_MESSAGE;
    }

    public String getAPI_GATEWAY_NAME() {
        return API_GATEWAY_NAME;
    }

    public String getAPI_GATEWAY_PORT() {
        return API_GATEWAY_PORT;
    }

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
