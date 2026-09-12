package com.medflow.appointmenthistoryservice.application.port.in.command;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentEventType;

import java.time.Instant;
import java.util.UUID;

/// Command carrying the appointment event details to be persisted as history.
public record RecordAppointmentEventCommand(
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
