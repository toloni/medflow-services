package com.medflow.appointmentservice.application.port.in.command;

import com.medflow.appointmentservice.domain.model.AppointmentStatus;

import java.time.Instant;

/// Input data for [com.medflow.appointmentservice.application.port.in.UpdateAppointmentUseCase];
/// a `null` field means that attribute is left unchanged.
public record UpdateAppointmentCommand(
        Instant scheduledAt,
        AppointmentStatus status,
        String notes
) {
}
