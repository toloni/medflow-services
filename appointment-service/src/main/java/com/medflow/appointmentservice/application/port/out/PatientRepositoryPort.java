package com.medflow.appointmentservice.application.port.out;

import com.medflow.appointmentservice.domain.model.Patient;

import java.util.Optional;
import java.util.UUID;

/// Output port for looking up [Patient] records.
public interface PatientRepositoryPort {

    Optional<Patient> findById(UUID patientId);

    Optional<Patient> findByUserId(UUID userId);
}
