package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.application.port.out.PatientRepositoryPort;
import com.medflow.appointmentservice.domain.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/// Adapts [PatientRepositoryPort] to Spring Data JPA via [PatientJpaRepository].
@Component
@RequiredArgsConstructor
class PatientRepositoryAdapter implements PatientRepositoryPort {

    private final PatientJpaRepository jpaRepository;

    @Override
    public Optional<Patient> findById(UUID patientId) {
        return jpaRepository.findById(patientId);
    }

    @Override
    public Optional<Patient> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId);
    }
}
