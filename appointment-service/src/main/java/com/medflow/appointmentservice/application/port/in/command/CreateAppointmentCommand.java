package com.medflow.appointmentservice.application.port.in.command;

import java.time.Instant;
import java.util.UUID;

/// Input data for [com.medflow.appointmentservice.application.port.in.CreateAppointmentUseCase].
public record CreateAppointmentCommand(
        UUID patientId,
        UUID doctorId,
        Instant scheduledAt,
        String notes
) {
}
