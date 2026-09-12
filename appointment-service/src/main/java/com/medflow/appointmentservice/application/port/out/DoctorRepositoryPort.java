package com.medflow.appointmentservice.application.port.out;

import com.medflow.appointmentservice.domain.model.Doctor;

import java.util.Optional;
import java.util.UUID;

/// Output port for looking up [Doctor] records.
public interface DoctorRepositoryPort {

    Optional<Doctor> findById(UUID doctorId);

    Optional<Doctor> findByUserId(UUID userId);
}
