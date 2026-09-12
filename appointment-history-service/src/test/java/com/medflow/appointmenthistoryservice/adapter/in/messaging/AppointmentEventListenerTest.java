package com.medflow.appointmenthistoryservice.adapter.in.messaging;

import com.medflow.appointmenthistoryservice.adapter.in.messaging.event.AppointmentEventPayload;
import com.medflow.appointmenthistoryservice.application.port.in.RecordAppointmentEventUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.command.RecordAppointmentEventCommand;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {

    @Mock
    private RecordAppointmentEventUseCase recordAppointmentEventUseCase;

    private AppointmentEventListener appointmentEventListener;

    @BeforeEach
    void setUp() {
        appointmentEventListener = new AppointmentEventListener(recordAppointmentEventUseCase);
    }

    @Test
    void onAppointmentEventMapsPayloadFieldsIntoCommandAndHandlesIt() {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        Instant scheduledAt = Instant.parse("2026-02-20T09:30:00Z");

        AppointmentEventPayload payload = new AppointmentEventPayload(
                AppointmentEventType.APPOINTMENT_UPDATED,
                appointmentId,
                patientId,
                "John Doe",
                doctorId,
                "Dr. House",
                scheduledAt,
                "CONFIRMED"
        );

        appointmentEventListener.onAppointmentEvent(payload);

        ArgumentCaptor<RecordAppointmentEventCommand> captor =
                ArgumentCaptor.forClass(RecordAppointmentEventCommand.class);
        verify(recordAppointmentEventUseCase).handle(captor.capture());
        verifyNoMoreInteractions(recordAppointmentEventUseCase);

        RecordAppointmentEventCommand command = captor.getValue();
        assertThat(command.eventType()).isEqualTo(payload.eventType());
        assertThat(command.appointmentId()).isEqualTo(payload.appointmentId());
        assertThat(command.patientId()).isEqualTo(payload.patientId());
        assertThat(command.patientName()).isEqualTo(payload.patientName());
        assertThat(command.doctorId()).isEqualTo(payload.doctorId());
        assertThat(command.doctorName()).isEqualTo(payload.doctorName());
        assertThat(command.scheduledAt()).isEqualTo(payload.scheduledAt());
        assertThat(command.status()).isEqualTo(payload.status());
    }
}
