package com.tlvlp.iot.server.unit.service.rpc;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.ModuleReport;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Reporter {

    private static final Logger log = LoggerFactory.getLogger(Reporter.class);
    private RestTemplate restTemplate;
    private Properties properties;

    public Reporter(RestTemplate restTemplate, Properties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void sendReport(Unit unit) {
        List<ModuleReport> payload = getReportList(unit);
        try {
            ResponseEntity<Map> response =  restTemplate.postForEntity(
                    properties.REPORTING_SERVICE_API_POST_VALUES_URL,
                    payload,
                    Map.class);
            log.debug("Module updates sent to the Reporting service: {}", response.getBody());
        } catch (ResourceAccessException e) {
            log.error("Reporting service is not responding: {}", e.getMessage());
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Failed to send Module updates to the Reporting service: {}", e.getResponseBodyAsString());
        }
    }

    private List<ModuleReport> getReportList(Unit unit) {
        return unit.getModules().stream()
                .map(this::createReport)
                .collect(Collectors.toList());
    }

    private ModuleReport createReport(Module module) {
        return new ModuleReport()
                .setModuleID(module.getModuleID())
                .setUnitID(module.getUnitID())
                .setValue(module.getValue());
    }
}
