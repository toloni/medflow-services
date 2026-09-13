package com.medflow.notificationservice.adapter.in.messaging;

import com.medflow.notificationservice.adapter.in.messaging.event.AppointmentEventPayload;
import com.medflow.notificationservice.application.port.in.SendAppointmentReminderUseCase;
import com.medflow.notificationservice.application.port.in.command.AppointmentEventCommand;
import com.medflow.notificationservice.domain.model.AppointmentEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {

    @Mock
    private SendAppointmentReminderUseCase sendAppointmentReminderUseCase;

    private AppointmentEventListener listener;

    @Test
    void onAppointmentEvent_mapsPayloadFieldsIntoCommand_andCallsHandle() {
        listener = new AppointmentEventListener(sendAppointmentReminderUseCase);

        AppointmentEventPayload payload = new AppointmentEventPayload(
                AppointmentEventType.APPOINTMENT_UPDATED,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Jane Doe",
                UUID.randomUUID(),
                "Dr. House",
                Instant.parse("2026-01-15T14:30:00Z"),
                "CANCELED"
        );

        listener.onAppointmentEvent(payload);

        ArgumentCaptor<AppointmentEventCommand> captor = ArgumentCaptor.forClass(AppointmentEventCommand.class);
        verify(sendAppointmentReminderUseCase).handle(captor.capture());

        AppointmentEventCommand command = captor.getValue();
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
