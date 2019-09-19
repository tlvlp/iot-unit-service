package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import com.tlvlp.iot.server.unit.service.persistence.UnitLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitLog service tests")
class UnitLogServiceTest {

    @Mock
    private UnitLogRepository repository;
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UnitLogService unitLogService;

    @Captor
    private ArgumentCaptor<UnitLog> captor;

    private Message message;

    @BeforeEach
    void beforeEach() {
        var payload = new HashMap<String, String>();
        payload.put("unitID", "unitID");
        payload.put("project", "project");
        payload.put("name", "name");
        payload.put("logEntry", "logEntry");
        message = new Message().setPayload(payload);
    }

    @Test
    void saveUnitLogInactiveFromMessageTest() {
        // given
        given(repository.save(any(UnitLog.class))).willReturn(new UnitLog());

        // when
        unitLogService.saveUnitLogInactiveFromMessage(message);

        // then
        then(repository).should().save(captor.capture());
        var savedLog = captor.getValue();

        sharedAssertionsForSave(savedLog);
        assertEquals("Unit became inactive", savedLog.getLogEntry(),
                "Inactive unit standard message is logged.");
    }

    @Test
    void saveUnitLogErrorFromMessageTest() {
        // given
        given(repository.save(any(UnitLog.class))).willReturn(new UnitLog());

        // when
        unitLogService.saveUnitLogErrorFromMessage(message);

        // then
        then(repository).should().save(captor.capture());
        var savedLog = captor.getValue();
        sharedAssertionsForSave(savedLog);
        assertEquals("logEntry", savedLog.getLogEntry(),
                "Unit error message details are logged.");
    }

    private void sharedAssertionsForSave(UnitLog savedLog) {
        assertNotNull(savedLog);
        assertNotNull(savedLog.getLogID());
        assertNotNull(savedLog.getArrived());
        assertTrue(savedLog.getLogID().contains("-LOG-"), "Generated ID contains '-LOG-'");
        assertEquals("unitID", savedLog.getUnitID(), "unitID field matches the provided value");
        assertEquals("project", savedLog.getProject(), "project field matches the provided value");
        assertEquals("name", savedLog.getName(), "name field matches the provided value");
    }

}