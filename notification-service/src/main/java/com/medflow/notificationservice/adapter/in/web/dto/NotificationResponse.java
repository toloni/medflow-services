package com.medflow.notificationservice.adapter.in.web.dto;

import com.medflow.notificationservice.domain.model.AppointmentEventType;
import com.medflow.notificationservice.domain.model.Notification;
import com.medflow.notificationservice.domain.model.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

/// REST representation of a [Notification] returned by the notifications API.
public record NotificationResponse(
        UUID id,
        UUID appointmentId,
        UUID patientId,
        String patientName,
        String doctorName,
        Instant scheduledAt,
        AppointmentEventType eventType,
        String channel,
        String message,
        NotificationStatus status,
        Instant createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getAppointmentId(),
                notification.getPatientId(),
                notification.getPatientName(),
                notification.getDoctorName(),
                notification.getScheduledAt(),
                notification.getEventType(),
                notification.getChannel(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
