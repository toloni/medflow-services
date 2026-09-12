package com.medflow.appointmentservice.adapter.in.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/// Request payload for creating a new appointment.
public record CreateAppointmentRequest(

        @NotNull(message = "patientId is required")
        UUID patientId,

        @NotNull(message = "doctorId is required")
        UUID doctorId,

        @NotNull(message = "scheduledAt is required")
        @Future(message = "scheduledAt must be in the future")
        Instant scheduledAt,

        @Size(max = 500, message = "notes must be at most 500 characters")
        String notes
) {
}
