package com.medflow.notificationservice.adapter.in.web.dto;

import com.medflow.notificationservice.domain.model.AppointmentEventType;
import com.medflow.notificationservice.domain.model.Notification;
import com.medflow.notificationservice.domain.model.NotificationStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationResponseTest {

    @Test
    void from_mapsAllFieldsFromNotification() {
        UUID id = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Instant scheduledAt = Instant.parse("2026-01-15T14:30:00Z");
        Instant createdAt = Instant.parse("2026-01-15T14:00:00Z");

        Notification notification = Notification.builder()
                .id(id)
                .appointmentId(appointmentId)
                .patientId(patientId)
                .patientName("Jane Doe")
                .doctorName("Dr. House")
                .scheduledAt(scheduledAt)
                .eventType(AppointmentEventType.APPOINTMENT_CREATED)
                .channel("EMAIL")
                .message("Hello Jane Doe")
                .status(NotificationStatus.SENT)
                .createdAt(createdAt)
                .build();

        NotificationResponse response = NotificationResponse.from(notification);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.appointmentId()).isEqualTo(appointmentId);
        assertThat(response.patientId()).isEqualTo(patientId);
        assertThat(response.patientName()).isEqualTo("Jane Doe");
        assertThat(response.doctorName()).isEqualTo("Dr. House");
        assertThat(response.scheduledAt()).isEqualTo(scheduledAt);
        assertThat(response.eventType()).isEqualTo(AppointmentEventType.APPOINTMENT_CREATED);
        assertThat(response.channel()).isEqualTo("EMAIL");
        assertThat(response.message()).isEqualTo("Hello Jane Doe");
        assertThat(response.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }
}
