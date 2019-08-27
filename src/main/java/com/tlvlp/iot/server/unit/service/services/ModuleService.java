package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.modules.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ModuleService {

    public Set<Module> parseModulesFromPayload(Map<String, String> payload, String unitID)
            throws IllegalArgumentException {
        Set<Module> modules = new HashSet<>();
        Map<String, String> payloadFiltered = filterPayload(payload);
        for (String key : payloadFiltered.keySet()) {
            try {
                String module_ref = key.split("\\|")[0];
                String module_name = key.split("\\|")[1];
                String module_value = payloadFiltered.get(key);
                switch (module_ref) {
                    case Relay.REFERENCE:
                        modules.add(new Relay()
                                .setModuleID(key)
                                .setName(module_name)
                                .setState(module_value.equals("on") ? Relay.State.on : Relay.State.off)
                                .setUnitID(unitID));
                        break;
                    case LightSensorGl5528.REFERENCE:
                        modules.add(new LightSensorGl5528()
                                .setModuleID(key)
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value))
                                .setUnitID(unitID));
                        break;
                    case SoilMoistureSensor.REFERENCE:
                        modules.add(new SoilMoistureSensor()
                                .setModuleID(key)
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value))
                                .setUnitID(unitID));
                        break;
                    case TempSensorDS18B20.REFERENCE:
                        modules.add(new TempSensorDS18B20()
                                .setModuleID(key)
                                .setName(module_name)
                                .setValue(Integer.parseInt(module_value))
                                .setUnitID(unitID));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unrecognized module reference: %s", module_ref));
                }
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
