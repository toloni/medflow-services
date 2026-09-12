package com.medflow.notificationservice.application.port.out;

import com.medflow.notificationservice.domain.model.Notification;

import java.util.List;
import java.util.UUID;

/// Output port for persisting and querying [Notification] records.
public interface NotificationRepositoryPort {
    Notification save(Notification notification);

    List<Notification> findAll();

    List<Notification> findByPatientId(UUID patientId);
}
