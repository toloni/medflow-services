package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.domain.model.AppointmentStatus;

import java.time.Instant;

public record UpdateAppointmentCommand(
        Instant scheduledAt,
        AppointmentStatus status,
        String notes
) {
}
