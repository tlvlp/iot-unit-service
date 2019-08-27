package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class UnitUpdateReporter {

    private static final Logger log = LoggerFactory.getLogger(UnitUpdateReporter.class);
    private RestTemplate restTemplate;
    private Properties properties;

    public UnitUpdateReporter(RestTemplate restTemplate, Properties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void sendUnitToReporting(Unit unit) {
        try {
            restTemplate.postForEntity(
                    properties.REPORTING_SERVICE_API_UNIT_UPDATE_URL,
                    unit,
                    String.class);
            log.debug("Unit update sent to the Reporting service: {}", unit);
        } catch (ResourceAccessException e) {
            log.error("Reporting service is not responding: {}", e.getMessage());
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Failed to send unit update to the Reporting service: {}", e.getResponseBodyAsString());
        }
    }
}
