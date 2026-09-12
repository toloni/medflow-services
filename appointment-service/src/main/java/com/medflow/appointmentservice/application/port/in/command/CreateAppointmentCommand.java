package com.medflow.appointmentservice.application.port.in;

import java.time.Instant;
import java.util.UUID;

public record CreateAppointmentCommand(
        UUID patientId,
        UUID doctorId,
        Instant scheduledAt,
        String notes
) {
}
