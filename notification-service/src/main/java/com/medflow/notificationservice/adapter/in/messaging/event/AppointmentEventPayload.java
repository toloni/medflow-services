package com.medflow.notificationservice.adapter.in.messaging.event;

import com.medflow.notificationservice.domain.model.AppointmentEventType;

import java.time.Instant;
import java.util.UUID;

/// Kafka message payload for an appointment domain event.
public record AppointmentEventPayload(
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
