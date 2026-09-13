package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.domain.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [Patient].
interface PatientJpaRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByUserId(UUID userId);
}