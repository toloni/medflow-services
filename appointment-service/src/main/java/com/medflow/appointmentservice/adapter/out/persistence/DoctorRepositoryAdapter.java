package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.application.port.out.DoctorRepositoryPort;
import com.medflow.appointmentservice.domain.model.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/// Adapts [DoctorRepositoryPort] to Spring Data JPA via [DoctorJpaRepository].
@Component
@RequiredArgsConstructor
class DoctorRepositoryAdapter implements DoctorRepositoryPort {

    private final DoctorJpaRepository jpaRepository;

    @Override
    public Optional<Doctor> findById(UUID doctorId) {
        return jpaRepository.findById(doctorId);
    }

    @Override
    public Optional<Doctor> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId);
    }
}
