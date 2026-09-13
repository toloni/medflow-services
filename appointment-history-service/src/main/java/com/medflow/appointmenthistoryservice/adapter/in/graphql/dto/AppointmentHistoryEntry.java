package com.medflow.appointmenthistoryservice.adapter.in.graphql.dto;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentEventType;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;

import java.time.Instant;
import java.util.UUID;

/// GraphQL representation of a single appointment history record.
public record AppointmentHistoryEntry(
        UUID id,
        UUID appointmentId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        Instant scheduledAt,
        AppointmentEventType eventType,
        String status,
        Instant recordedAt
) {
    public static AppointmentHistoryEntry from(AppointmentHistory history) {
        return new AppointmentHistoryEntry(
                history.getId(),
                history.getAppointmentId(),
                history.getPatientId(),
                history.getPatientName(),
                history.getDoctorId(),
                history.getDoctorName(),
                history.getScheduledAt(),
                history.getEventType(),
                history.getStatus(),
                history.getRecordedAt()
        );
    }
}
