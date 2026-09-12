package com.medflow.appointmenthistoryservice.adapter.in.graphql;

import com.medflow.appointmenthistoryservice.adapter.in.graphql.dto.AppointmentHistoryEntry;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryByAppointmentUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryByPatientUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryUseCase;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentEventType;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentHistoryGraphQlControllerTest {

    @Mock
    private ListAppointmentHistoryUseCase listAppointmentHistoryUseCase;

    @Mock
    private ListAppointmentHistoryByAppointmentUseCase listAppointmentHistoryByAppointmentUseCase;

    @Mock
    private ListAppointmentHistoryByPatientUseCase listAppointmentHistoryByPatientUseCase;

    private AppointmentHistoryGraphQlController controller;

    @BeforeEach
    void setUp() {
        controller = new AppointmentHistoryGraphQlController(
                listAppointmentHistoryUseCase,
                listAppointmentHistoryByAppointmentUseCase,
                listAppointmentHistoryByPatientUseCase
        );
    }

    private AppointmentHistory sampleHistory() {
        return AppointmentHistory.builder()
                .id(UUID.randomUUID())
                .appointmentId(UUID.randomUUID())
                .patientId(UUID.randomUUID())
                .patientName("Jane Doe")
                .doctorId(UUID.randomUUID())
                .doctorName("Dr. Smith")
                .scheduledAt(Instant.parse("2026-03-01T08:00:00Z"))
                .eventType(AppointmentEventType.APPOINTMENT_CREATED)
                .status("SCHEDULED")
                .recordedAt(Instant.parse("2026-03-01T08:05:00Z"))
                .build();
    }

    @Test
    void appointmentHistoriesDelegatesToListUseCaseAndMapsResult() {
        AppointmentHistory history = sampleHistory();
        when(listAppointmentHistoryUseCase.list()).thenReturn(List.of(history));

        List<AppointmentHistoryEntry> result = controller.appointmentHistories();

        assertThat(result).containsExactly(AppointmentHistoryEntry.from(history));
        verify(listAppointmentHistoryUseCase).list();
        verifyNoMoreInteractions(listAppointmentHistoryUseCase);
        verifyNoInteractions(listAppointmentHistoryByAppointmentUseCase, listAppointmentHistoryByPatientUseCase);
    }

    @Test
    void appointmentHistoryByAppointmentDelegatesToUseCaseAndMapsResult() {
        UUID appointmentId = UUID.randomUUID();
        AppointmentHistory history = sampleHistory();
        when(listAppointmentHistoryByAppointmentUseCase.listByAppointment(appointmentId))
                .thenReturn(List.of(history));

        List<AppointmentHistoryEntry> result = controller.appointmentHistoryByAppointment(appointmentId);

        assertThat(result).containsExactly(AppointmentHistoryEntry.from(history));
        verify(listAppointmentHistoryByAppointmentUseCase).listByAppointment(appointmentId);
        verifyNoMoreInteractions(listAppointmentHistoryByAppointmentUseCase);
        verifyNoInteractions(listAppointmentHistoryUseCase, listAppointmentHistoryByPatientUseCase);
    }

    @Test
    void appointmentHistoriesByPatientDelegatesToUseCaseAndMapsResult() {
        UUID patientId = UUID.randomUUID();
        AppointmentHistory history = sampleHistory();
        when(listAppointmentHistoryByPatientUseCase.listByPatient(patientId))
                .thenReturn(List.of(history));

        List<AppointmentHistoryEntry> result = controller.appointmentHistoriesByPatient(patientId);

        assertThat(result).containsExactly(AppointmentHistoryEntry.from(history));
        verify(listAppointmentHistoryByPatientUseCase).listByPatient(patientId);
        verifyNoMoreInteractions(listAppointmentHistoryByPatientUseCase);
        verifyNoInteractions(listAppointmentHistoryUseCase, listAppointmentHistoryByAppointmentUseCase);
    }
}
