package com.tlvlp.iot.server.unit.service.services;

import com.tlvlp.iot.server.unit.service.config.Properties;
import com.tlvlp.iot.server.unit.service.persistence.Message;
import com.tlvlp.iot.server.unit.service.persistence.Unit;
import com.tlvlp.iot.server.unit.service.persistence.UnitLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Incoming Message Handler tests")
class IncomingMessageHandlerTest {

    @Mock
    private Properties properties;
    @Mock
    private UnitService unitService;
    @Mock
    private UnitLogService unitLogService;

    @InjectMocks
    private IncomingMessageHandler incomingMessageHandler;

    @Captor
    private ArgumentCaptor<Unit> unitCaptor;

    private String testedTopic;
    private Message incomingMessage;
    private UnitLog unitLog;
    private Unit unit;
    private Map<String, String> payloadMap;

    @BeforeEach
    void beforeEach() {
        unitLog = new UnitLog();
        unit = new Unit();
        testedTopic = "topic";

        payloadMap = new HashMap<>();
        payloadMap.put("unitID", "unitID");
        payloadMap.put("name", "name");
        payloadMap.put("project", "project");

        incomingMessage = new Message()
                .setTopic(testedTopic)
                .setPayload(payloadMap);

    }

    @Test
    @DisplayName("Handle incoming error message from Unit")
    void handleIncomingMessageErrorTopic() {
        // given
        payloadMap.put("error", "error");
        incomingMessage.setPayload(payloadMap);
        given(properties.getMCU_MQTT_TOPIC_GLOBAL_ERROR()).willReturn(testedTopic);
        given(unitLogService.saveUnitLogErrorFromMessage(any(Message.class))).willReturn(unitLog);

        // when
        var responseMap = incomingMessageHandler.handleIncomingMessage(incomingMessage);

        // then
        then(properties).should().getMCU_MQTT_TOPIC_GLOBAL_ERROR();
        assertNotNull(responseMap);
        assertEquals("error", responseMap.get(IncomingMessageHandler.RESPONSE_TYPE),
                "The response's type field should be error");
        assertEquals(unitLog, responseMap.get(IncomingMessageHandler.RESPONSE_OBJECT),
                "The response's result field should contain the generated log entry");

    }

    @Test
    @DisplayName("Handle incoming inactive message from Unit")
    void handleIncomingMessageInactiveTopic() {
        // given
        given(properties.getMCU_MQTT_TOPIC_GLOBAL_INACTIVE()).willReturn(testedTopic);
        given(unitService.getUnitByID(anyString())).willReturn(Optional.of(unit));
        given(unitLogService.saveUnitLogInactiveFromMessage(any(Message.class))).willReturn(unitLog);

        // when
        var responseMap = incomingMessageHandler.handleIncomingMessage(incomingMessage);

        // then
        then(properties).should().getMCU_MQTT_TOPIC_GLOBAL_INACTIVE();
        then(unitService).should().getUnitByID(anyString());
        then(unitService).should().saveUnit(unitCaptor.capture());

        var savedUnit = unitCaptor.getValue();
        assertNotNull(savedUnit);
        assertFalse(savedUnit.isActive(), "The saved inactive Unit's status should be inactive");

        assertNotNull(responseMap);
        assertEquals("inactive", responseMap.get(IncomingMessageHandler.RESPONSE_TYPE),
                "The response's type field should be inactive");
        assertEquals(unitLog, responseMap.get(IncomingMessageHandler.RESPONSE_OBJECT),
                "The response's result field should contain the generated log entry");

    }

    @Test
    @DisplayName("Handle incoming inactive message for non-existent Unit")
    void handleIncomingMessageInactiveTopicMissingUnit() {
        // given
        given(properties.getMCU_MQTT_TOPIC_GLOBAL_INACTIVE()).willReturn(testedTopic);
        given(unitService.getUnitByID(anyString())).willReturn(Optional.empty());

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> incomingMessageHandler.handleIncomingMessage(incomingMessage),
                "Throw error if unit is not found in the database");
    }

    @Test
    @DisplayName("Handle incoming status change from a new Unit")
    void handleIncomingMessageStatusTopicNewUnit() {
        // given
        given(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS()).willReturn(testedTopic);
        given(unitService.getUnitByID(anyString())).willReturn(Optional.empty());
        given(unitService.createUnitFromMessage(any(Message.class))).willReturn(unit);

        // when
        var responseMap = incomingMessageHandler.handleIncomingMessage(incomingMessage);

        // then
        then(properties).should().getMCU_MQTT_TOPIC_GLOBAL_STATUS();
        then(unitService).should().getUnitByID(anyString());
        then(unitService).should().createUnitFromMessage(any(Message.class));

        assertNotNull(responseMap);
        assertEquals("status", responseMap.get(IncomingMessageHandler.RESPONSE_TYPE),
                "The response's type field should be status");
        assertEquals(unit, responseMap.get(IncomingMessageHandler.RESPONSE_OBJECT),
                "The response's result field should contain the saved Unit");

    }

    @Test
    @DisplayName("Handle incoming status change from an existing Unit")
    void handleIncomingMessageStatusTopicUpdateUnit() {
        // given
        given(properties.getMCU_MQTT_TOPIC_GLOBAL_STATUS()).willReturn(testedTopic);
        given(unitService.getUnitByID(anyString())).willReturn(Optional.of(unit));
        given(unitService.updateUnitFromMessage(any(Unit.class), any(Message.class))).willReturn(unit);

        // when
        var responseMap = incomingMessageHandler.handleIncomingMessage(incomingMessage);

        // then
        then(properties).should().getMCU_MQTT_TOPIC_GLOBAL_STATUS();
        then(unitService).should().getUnitByID(anyString());
        then(unitService).should().updateUnitFromMessage(any(Unit.class), any(Message.class));

        assertNotNull(responseMap);
        assertEquals("status", responseMap.get(IncomingMessageHandler.RESPONSE_TYPE),
                "The response's type field should be status");
        assertEquals(unit, responseMap.get(IncomingMessageHandler.RESPONSE_OBJECT),
                "The response's result field should contain the saved Unit");
    }

    @Test
    @DisplayName("Handle incoming message with invalid topic")
    void handleIncomingMessageWithInvalidTopic() {
        // given
        incomingMessage.setTopic(null);

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> incomingMessageHandler.handleIncomingMessage(incomingMessage),
                "Throw error for invalid topic");
    }

    @Test
    @DisplayName("Handle incoming message with invalid unitID")
    void handleIncomingMessageWithInvalidUnitID() {
        // given
        payloadMap.replace("unitID", null);
        incomingMessage.setPayload(payloadMap);

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> incomingMessageHandler.handleIncomingMessage(incomingMessage),
                "Throw error for invalid unitID");
    }

    @Test
    @DisplayName("Handle incoming message with invalid Unit name")
    void handleIncomingMessageWithInvalidName() {
        // given
        payloadMap.replace("name", null);
        incomingMessage.setPayload(payloadMap);

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> incomingMessageHandler.handleIncomingMessage(incomingMessage),
                "Throw error for invalid name");
    }

    @Test
    @DisplayName("Handle incoming message with invalid Unit project")
    void handleIncomingMessageWithInvalidProject() {
        // given
        payloadMap.replace("project", null);
        incomingMessage.setPayload(payloadMap);

        // when / then
        assertThrows(
                IllegalArgumentException.class,
                () -> incomingMessageHandler.handleIncomingMessage(incomingMessage),
                "Throw error for invalid Unit project");
    }
}