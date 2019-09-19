package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit service tests")
class UnitServiceTest {

    @Mock
    private UnitRepository repository;
    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private UnitService unitService;

    @Captor
    private ArgumentCaptor<Unit> captor;

    private Message message;
    private Unit oldUnit;
    private LocalDateTime oldDate;

    @BeforeEach
    void beforeEach() {
        var payload = new HashMap<String, String>();
        payload.put("unitID", "unitID");
        payload.put("project", "project");
        payload.put("name", "name");
        payload.put("module|module_name", "22.0");
        message = new Message().setPayload(payload);
        oldDate = LocalDateTime.now().minusDays(1);
        oldUnit = new Unit()
                .setUnitID("unitID")
                .setProject("project")
                .setName("name")
                .setControlTopic("control_topic")
                .setActive(false)
                .setLastSeen(oldDate)
                .setModules(Collections.emptySet());
    }

    private void sharedUnitAssertions(Unit savedUnit) {
        assertNotNull(savedUnit);
        assertNotNull(savedUnit.getLastSeen(), "Last seen time is updated");
        assertTrue(savedUnit.isActive(), "Status set to active");
        assertEquals("unitID", savedUnit.getUnitID(), "unitID field matches the provided value");
        assertEquals("project", savedUnit.getProject(), "project field matches the provided value");
        assertEquals("name", savedUnit.getName(), "name field matches the provided value");
    }

    @Test
    @DisplayName("Create Unit from Message")
    void createUnitFromMessageTest() {
        // given
        given(moduleService.parseModulesFromPayload(anyMap(), anyString())).willReturn(Collections.emptySet());

        // when
        unitService.createUnitFromMessage(message);

        // then
        then(moduleService).should().parseModulesFromPayload(anyMap(), anyString());
        then(repository).should().save(captor.capture());

        var savedUnit = captor.getValue();
        sharedUnitAssertions(savedUnit);
        assertNotNull(savedUnit.getUnitID(), "unitID is set for new Unit");
        assertNotNull(savedUnit.getControlTopic(), "Control topic is generated");
    }

    @Test
    @DisplayName("Update Unit from Message")
    void updateUnitFromMessageTest() {
        // given
        var modules = new HashSet<Module>();
        modules.add(new Module());
        given(moduleService.parseModulesFromPayload(anyMap(), anyString())).willReturn(modules);
        given(repository.save(any(Unit.class))).willReturn(oldUnit);

        // when
        unitService.updateUnitFromMessage(oldUnit, message);

        // then
        then(moduleService).should().parseModulesFromPayload(anyMap(), anyString());
        then(repository).should().save(captor.capture());

        var savedUnit = captor.getValue();
        sharedUnitAssertions(savedUnit);
        assertTrue(savedUnit.getLastSeen().isAfter(oldDate), "Last seen time is updated");
        assertFalse(savedUnit.getModules().isEmpty(), "New module is added");
    }
}