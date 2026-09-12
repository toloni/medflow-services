package com.medflow.appointmenthistoryservice.application.service;

import com.medflow.appointmenthistoryservice.application.port.in.command.RecordAppointmentEventCommand;
import com.medflow.appointmenthistoryservice.application.port.out.AppointmentHistoryRepositoryPort;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentEventType;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentHistoryServiceTest {

    @Mock
    private AppointmentHistoryRepositoryPort appointmentHistoryRepository;

    private AppointmentHistoryService appointmentHistoryService;

    @BeforeEach
    void setUp() {
        appointmentHistoryService = new AppointmentHistoryService(appointmentHistoryRepository);
    }

    @Test
    void handleBuildsAppointmentHistoryFromCommandAndSavesIt() {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        Instant scheduledAt = Instant.parse("2026-01-15T10:00:00Z");

        RecordAppointmentEventCommand command = new RecordAppointmentEventCommand(
                AppointmentEventType.APPOINTMENT_CREATED,
                appointmentId,
                patientId,
                "Jane Doe",
                doctorId,
                "Dr. Smith",
                scheduledAt,
                "SCHEDULED"
        );

        appointmentHistoryService.handle(command);

        ArgumentCaptor<AppointmentHistory> captor = ArgumentCaptor.forClass(AppointmentHistory.class);
        verify(appointmentHistoryRepository).save(captor.capture());
        verifyNoMoreInteractions(appointmentHistoryRepository);

        AppointmentHistory saved = captor.getValue();
        assertThat(saved.getAppointmentId()).isEqualTo(appointmentId);
        assertThat(saved.getPatientId()).isEqualTo(patientId);
        assertThat(saved.getPatientName()).isEqualTo("Jane Doe");
        assertThat(saved.getDoctorId()).isEqualTo(doctorId);
        assertThat(saved.getDoctorName()).isEqualTo("Dr. Smith");
        assertThat(saved.getScheduledAt()).isEqualTo(scheduledAt);
        assertThat(saved.getEventType()).isEqualTo(AppointmentEventType.APPOINTMENT_CREATED);
        assertThat(saved.getStatus()).isEqualTo("SCHEDULED");
    }

    @Test
    void listDelegatesToRepositoryFindAll() {
        AppointmentHistory history = AppointmentHistory.builder().id(UUID.randomUUID()).build();
        List<AppointmentHistory> expected = List.of(history);
        when(appointmentHistoryRepository.findAll()).thenReturn(expected);

        List<AppointmentHistory> result = appointmentHistoryService.list();

        assertThat(result).isEqualTo(expected);
        verify(appointmentHistoryRepository).findAll();
        verifyNoMoreInteractions(appointmentHistoryRepository);
    }

    @Test
    void listByAppointmentDelegatesToRepositoryFindByAppointmentId() {
        UUID appointmentId = UUID.randomUUID();
        AppointmentHistory history = AppointmentHistory.builder().id(UUID.randomUUID()).build();
        List<AppointmentHistory> expected = List.of(history);
        when(appointmentHistoryRepository.findByAppointmentId(appointmentId)).thenReturn(expected);

        List<AppointmentHistory> result = appointmentHistoryService.listByAppointment(appointmentId);

        assertThat(result).isEqualTo(expected);
        verify(appointmentHistoryRepository).findByAppointmentId(appointmentId);
        verifyNoMoreInteractions(appointmentHistoryRepository);
    }

    @Test
    void listByPatientDelegatesToRepositoryFindByPatientId() {
        UUID patientId = UUID.randomUUID();
        AppointmentHistory history = AppointmentHistory.builder().id(UUID.randomUUID()).build();
        List<AppointmentHistory> expected = List.of(history);
        when(appointmentHistoryRepository.findByPatientId(patientId)).thenReturn(expected);

        List<AppointmentHistory> result = appointmentHistoryService.listByPatient(patientId);

        assertThat(result).isEqualTo(expected);
        verify(appointmentHistoryRepository).findByPatientId(patientId);
        verifyNoMoreInteractions(appointmentHistoryRepository);
    }
}
