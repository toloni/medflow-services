package com.medflow.appointmentservice.adapter.in.web.dto;

import com.medflow.appointmentservice.domain.model.Appointment;
import com.medflow.appointmentservice.domain.model.AppointmentStatus;

import java.time.Instant;
import java.util.UUID;

/// API representation of an appointment, including denormalized patient and
/// doctor names for display.
public record AppointmentResponse(

        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        Instant scheduledAt,
        AppointmentStatus status,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {

    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getFullName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getFullName(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
