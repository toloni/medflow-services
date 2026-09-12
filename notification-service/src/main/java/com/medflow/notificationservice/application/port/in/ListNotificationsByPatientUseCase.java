package com.medflow.notificationservice.application.port.in;

import com.medflow.notificationservice.domain.model.Notification;

import java.util.List;
import java.util.UUID;

/// Use case for retrieving all notifications recorded for a given patient.
public interface ListNotificationsByPatientUseCase {
    List<Notification> listByPatient(UUID patientId);
}
