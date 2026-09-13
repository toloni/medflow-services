package com.medflow.appointmentservice.adapter.out.messaging.event;

import com.medflow.appointmentservice.application.port.out.event.AppointmentEventType;

import java.time.Instant;
import java.util.UUID;

/// Kafka message payload describing an appointment lifecycle event.
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
