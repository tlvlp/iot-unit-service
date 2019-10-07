package com.tlvlp.iot.server.unit.service.services;

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

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit service Scheduled Event tests")
class UnitServiceScheduledEventsTest {

    @Mock
    private UnitRepository repository;

    @InjectMocks
    private UnitService unitService;

    @Captor
    private ArgumentCaptor<Unit> captor;

    private Unit oldUnit;

    @BeforeEach
    void beforeEach() {
        var scheduledEvents = new HashSet<String>();
        scheduledEvents.add("event1");
        scheduledEvents.add("event2");
        oldUnit = new Unit()
                .setUnitID("unitID")
                .setScheduledEvents(scheduledEvents);
    }

    void sharedScheduledEventAssertions(Unit savedUnit) {
        assertNotNull(savedUnit);
        assertNotNull(savedUnit.getScheduledEvents());
    }

    @Test
    @DisplayName("Add scheduled event to Unit")
    void modifyUnitScheduledEventListTestAdd() throws UnitProcessingException {
        // given
        var newEventID = "event3";
        var requestDetails = Map.of("unitID", oldUnit.getUnitID(), "eventID", newEventID);
        given(repository.findById(anyString())).willReturn(Optional.of(oldUnit));

        // when
        unitService.modifyUnitScheduledEventList(requestDetails, false);

        // then
        then(repository).should().findById(oldUnit.getUnitID());
        then(repository).should().save(captor.capture());

        var savedUnit = captor.getValue();
        sharedScheduledEventAssertions(savedUnit);
        var updatedEvents = savedUnit.getScheduledEvents();
        assertTrue(updatedEvents.contains(newEventID));
        assertEquals(3, updatedEvents.size());
    }

    @Test
    @DisplayName("Remove scheduled event from Unit")
    void modifyUnitScheduledEventListTestDelete() throws UnitProcessingException {
        // given
        var eventIDToRemove = "event2";
        var requestDetails = Map.of("unitID", oldUnit.getUnitID(), "eventID", eventIDToRemove);
        given(repository.findById(anyString())).willReturn(Optional.of(oldUnit));

        // when
        unitService.modifyUnitScheduledEventList(requestDetails, true);

        // then
        then(repository).should().findById(oldUnit.getUnitID());
        then(repository).should().save(captor.capture());

        var savedUnit = captor.getValue();
        sharedScheduledEventAssertions(savedUnit);
        var updatedEvents = savedUnit.getScheduledEvents();
        assertFalse(updatedEvents.contains(eventIDToRemove));
        assertEquals(1, updatedEvents.size());

    }

    @Test
    @DisplayName("Modify scheduled event and get error when the unitID is not in the database")
    void modifyUnitScheduledEventListTestError() {
        // given
        var requestDetails = Map.of("unitID", "unitID", "eventID", "eventID");
        given(repository.findById(anyString())).willReturn(Optional.empty());

        // when / then
        assertThrows(
                UnitProcessingException.class,
                () -> unitService.modifyUnitScheduledEventList(requestDetails, false),
                "Throw error if the Unit is not in the database"
        );


    }
}