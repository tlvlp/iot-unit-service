package com.tlvlp.iot.server.unit.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Properties {

    // The service uses environment variables from the Docker container.

    @Value("${MQTT_CLIENT_API_OUTGOING_MESSAGE_URL}")
    public String MQTT_CLIENT_API_OUTGOING_MESSAGE_URL;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS}")
    public String MCU_MQTT_TOPIC_GLOBAL_STATUS;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST}")
    public String MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_INACTIVE}")
    public String MCU_MQTT_TOPIC_GLOBAL_INACTIVE;

    @Value("${MCU_MQTT_TOPIC_GLOBAL_ERROR}")
    public String MCU_MQTT_TOPIC_GLOBAL_ERROR;

    @Value("${REPORTING_SERVICE_API_UNIT_UPDATE_URL}")
    public String REPORTING_SERVICE_API_UNIT_UPDATE_URL;


}
