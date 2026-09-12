package com.medflow.notificationservice.adapter.out.persistence;

import com.medflow.notificationservice.application.port.out.NotificationRepositoryPort;
import com.medflow.notificationservice.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/// Adapts the [NotificationRepositoryPort] output port to Spring Data JPA via
/// [NotificationJpaRepository].
@Component
@RequiredArgsConstructor
class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public List<Notification> findAll() {
        return notificationJpaRepository.findAll();
    }

    @Override
    public List<Notification> findByPatientId(UUID patientId) {
        return notificationJpaRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }
}
