package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Module;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Outgoing Message Composer tests")
class OutgoingMessageComposerTest {

    @Mock
    private Properties properties;
    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private OutgoingMessageComposer outgoingMessageComposer;

    private String unitID;
    private String controlTopic;
    private String moduleID;
    private Double value;
    private Module module;
    private Unit unit;

    @BeforeEach
    void beforeEach() {
        unitID = "unitID";
        controlTopic = "controlTopic";
        moduleID = "moduleID";
        value = 0d;
        module = new Module()
                .setUnitID(unitID)
                .setModuleID(moduleID)
                .setValue(value);
        unit = new Unit()
                .setUnitID(unitID)
                .setControlTopic(controlTopic)
                .setModules(Set.of(module));
    }

    @Test
    @DisplayName("Compose global status request message")
    void composeGlobalStatusRequest() {
        // given
        given(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST()).willCallRealMethod();

        // when
        var message = outgoingMessageComposer.composeGlobalStatusRequest();

        // then
        then(properties).should().getMCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST();
        assertNotNull(message);
        assertTrue(message.getPayload().isEmpty(), "The payload should empty for the global status request");
        assertEquals(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST(), message.getTopic(),
                "The topic is the global status request topic");
    }

    @Test
    @DisplayName("Compose module control message with valid details")
    void composeModuleControlMessageWithValidDetails() {
        // given
        given(unitRepository.findById(unitID)).willReturn(Optional.of(unit));

        // when
        var message = outgoingMessageComposer.composeModuleControlMessage(module);

        //then
        then(unitRepository).should().findById(unitID);
        assertNotNull(message);
        assertEquals(controlTopic, message.getTopic(), "The message's topic is the unit's control topic");
        assertEquals(value.toString(), message.getPayload().get(moduleID),
                "The message payload contains the module ID as the key and the new value as value");
    }

    @Test
    @DisplayName("Compose module control message with a unitID that is not in the DB")
    void composeModuleControlMessageWithUnitNotFound() {
        // given
        given(unitRepository.findById(anyString())).willReturn(Optional.empty());

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> outgoingMessageComposer.composeModuleControlMessage(module),
                "Throw exception if the Unit of the Module is not in the database");

    }

    @Test
    @DisplayName("Compose module control message with a module that is not in the Unit's modules")
    void composeModuleControlMessageWithModuleNotFound() {
        // given
        unit.setModules(Set.of());
        given(unitRepository.findById(unitID)).willReturn(Optional.of(unit));

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> outgoingMessageComposer.composeModuleControlMessage(module),
                "Throw exception if the Module is not in the retrieved Unit's module list");
    }
}