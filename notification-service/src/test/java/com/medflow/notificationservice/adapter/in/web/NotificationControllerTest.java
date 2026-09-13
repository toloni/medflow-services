package com.medflow.notificationservice.adapter.in.web;

import com.medflow.notificationservice.adapter.in.web.dto.NotificationResponse;
import com.medflow.notificationservice.application.port.in.ListNotificationsByPatientUseCase;
import com.medflow.notificationservice.application.port.in.ListNotificationsUseCase;
import com.medflow.notificationservice.domain.model.AppointmentEventType;
import com.medflow.notificationservice.domain.model.Notification;
import com.medflow.notificationservice.domain.model.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private ListNotificationsUseCase listNotificationsUseCase;

    @Mock
    private ListNotificationsByPatientUseCase listNotificationsByPatientUseCase;

    private NotificationController controller;

    @BeforeEach
    void setUp() {
        controller = new NotificationController(listNotificationsUseCase, listNotificationsByPatientUseCase);
    }

    private Notification sampleNotification(UUID patientId) {
        return Notification.builder()
                .id(UUID.randomUUID())
                .appointmentId(UUID.randomUUID())
                .patientId(patientId)
                .patientName("Jane Doe")
                .doctorName("Dr. House")
                .scheduledAt(Instant.parse("2026-01-15T14:30:00Z"))
                .eventType(AppointmentEventType.APPOINTMENT_CREATED)
                .channel("EMAIL")
                .message("Hello Jane Doe")
                .status(NotificationStatus.SENT)
                .createdAt(Instant.parse("2026-01-15T14:00:00Z"))
                .build();
    }

    @Test
    void list_delegatesToUseCase_andMapsToResponse() {
        Notification notification = sampleNotification(UUID.randomUUID());
        when(listNotificationsUseCase.list()).thenReturn(List.of(notification));

        ResponseEntity<List<NotificationResponse>> response = controller.list();

        verify(listNotificationsUseCase).list();
        verifyNoInteractions(listNotificationsByPatientUseCase);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(NotificationResponse.from(notification));
    }

    @Test
    void listByPatient_delegatesToUseCase_andMapsToResponse() {
        UUID patientId = UUID.randomUUID();
        Notification notification = sampleNotification(patientId);
        when(listNotificationsByPatientUseCase.listByPatient(patientId)).thenReturn(List.of(notification));

        ResponseEntity<List<NotificationResponse>> response = controller.listByPatient(patientId);

        verify(listNotificationsByPatientUseCase).listByPatient(patientId);
        verifyNoInteractions(listNotificationsUseCase);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(NotificationResponse.from(notification));
    }
}
