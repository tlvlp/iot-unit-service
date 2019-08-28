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
        Map<String, String> payloadFiltered = filterPayload(payload);
        for (String key : payloadFiltered.keySet()) {
            try {
                String module_ref = key.split("\\|")[0];
                String module_name = key.split("\\|")[1];
                Double module_value = Double.parseDouble(payloadFiltered.get(key));
                modules.add(new Module()
                        .setModuleID(key)
                        .setModule(module_ref)
                        .setName(module_name)
                        .setValue(module_value)
                        .setUnitID(unitID));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Malformed module reference in payload");
            }
        }
        return modules;
    }

    private Map<String, String> filterPayload(Map<String, String> payload) {
        Map<String, String> payloadFiltered = new HashMap<>();
        for (String key : payload.keySet()) {
            if (key.contains("|")) {
                payloadFiltered.put(key, payload.get(key));
            }
        }
        return payloadFiltered;
    }

}
