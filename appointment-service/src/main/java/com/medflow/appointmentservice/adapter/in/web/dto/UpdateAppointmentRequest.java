package com.medflow.appointmentservice.adapter.in.web.dto;

import com.medflow.appointmentservice.domain.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/// Request payload for partially updating an appointment; a `null` field
/// means that attribute is left unchanged.
public record UpdateAppointmentRequest(

        @Future(message = "scheduledAt must be in the future")
        Instant scheduledAt,

        AppointmentStatus status,

        @Size(max = 500, message = "notes must be at most 500 characters")
        String notes
) {
}