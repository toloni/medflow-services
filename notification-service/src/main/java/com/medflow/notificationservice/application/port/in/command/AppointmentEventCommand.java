package com.medflow.notificationservice.application.port.in.command;

import com.medflow.notificationservice.domain.model.AppointmentEventType;

import java.time.Instant;
import java.util.UUID;

/// Command carrying appointment event details into [com.medflow.notificationservice.application.port.in.SendAppointmentReminderUseCase].
public record AppointmentEventCommand(
        AppointmentEventType eventType,
        UUID appointmentId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        Instant scheduledAt,
        String status
) {
}
