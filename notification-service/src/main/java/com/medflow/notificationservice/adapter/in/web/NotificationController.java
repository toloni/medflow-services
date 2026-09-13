package com.medflow.notificationservice.adapter.in.web;

import com.medflow.notificationservice.adapter.in.web.dto.NotificationResponse;
import com.medflow.notificationservice.application.port.in.ListNotificationsByPatientUseCase;
import com.medflow.notificationservice.application.port.in.ListNotificationsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/// Exposes read-only REST endpoints for querying notifications, either across
/// all patients or filtered by a specific patient.
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ListNotificationsUseCase listNotificationsUseCase;
    private final ListNotificationsByPatientUseCase listNotificationsByPatientUseCase;

    /// Returns every recorded notification.
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list() {
        List<NotificationResponse> response = listNotificationsUseCase.list().stream()
                .map(NotificationResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    /// Returns the notifications recorded for the given patient, most recent first.
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NotificationResponse>> listByPatient(@PathVariable UUID patientId) {
        List<NotificationResponse> response = listNotificationsByPatientUseCase.listByPatient(patientId).stream()
                .map(NotificationResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
