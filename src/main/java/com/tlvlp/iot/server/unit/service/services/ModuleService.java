package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Module;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
class ModuleService {

    Set<Module> parseModulesFromPayload(Map<String, String> payload, String unitID)
            throws IllegalArgumentException {
        Set<Module> modules = new HashSet<>();
        Map<String, String> payloadFiltered = filterModules(payload);
        for (String key : payloadFiltered.keySet()) {
            modules.add(new Module()
                    .setModuleID(key)
                    .setValue(Double.parseDouble(payloadFiltered.get(key)))
                    .setUnitID(unitID));
        }
        return modules;
    }

    private Map<String, String> filterModules(Map<String, String> payload) {
        Map<String, String> payloadFiltered = new HashMap<>();
        for (String key : payload.keySet()) {
            if (key.contains("|")) {
                payloadFiltered.put(key, payload.get(key));
            }
        }
        return payloadFiltered;
    }

}
