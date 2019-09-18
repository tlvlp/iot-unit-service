package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Module service tests")
class ModuleServiceTest {

    @InjectMocks
    private ModuleService moduleService;

    private String unitID;
    private Map<String, String> payload;

    @BeforeEach
    void beforeEach() {
        unitID = "unitID";
        payload = new HashMap<>();
    }

    @Test
    @DisplayName("Parse modules from payload with correct values")
    void parseModulesFromPayloadTest() {
        // given
        payload.put("module1|module1_name", "22.0");
        payload.put("module2|module2_name", "11");
        payload.put("noseparator", "1");

        // when
        Set<Module> modules = moduleService.parseModulesFromPayload(payload, unitID);

        // then
        assertNotNull(modules);
        assertTrue(
                modules.containsAll(
                        Arrays.asList(
                                new Module()
                                        .setUnitID(unitID)
                                        .setModuleID("module1|module1_name")
                                        .setValue(22d),
                                new Module()
                                        .setUnitID(unitID)
                                        .setModuleID("module2|module2_name")
                                        .setValue(11d)
                        )
                ),
                "The payload entries with a separator are correctly parsed as modules"
        );
        assertFalse(modules.contains(
                new Module()
                        .setUnitID(unitID)
                        .setModuleID("no_separator")
                        .setValue(1d)),
                "Payload entry without a separator should not be parsed as a Module"
        );
    }

    @Test
    @DisplayName("Parse modules from payload with wrong value")
    void parseModulesFromPayloadTestError() {
        // given
        payload.put("module1|module1_name", "not_double");

        // when - then
        assertThrows(IllegalArgumentException.class,
                () -> moduleService.parseModulesFromPayload(payload, unitID));
    }


}