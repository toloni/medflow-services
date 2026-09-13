package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.domain.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [Doctor].
interface DoctorJpaRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByUserId(UUID userId);
}