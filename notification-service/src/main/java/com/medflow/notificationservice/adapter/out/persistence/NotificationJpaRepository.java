package com.medflow.notificationservice.adapter.out.persistence;

import com.medflow.notificationservice.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/// Spring Data JPA repository for [Notification] entities.
interface NotificationJpaRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
}
