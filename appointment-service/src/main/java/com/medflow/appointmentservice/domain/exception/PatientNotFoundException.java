package com.medflow.appointmentservice.domain.exception;

import java.util.UUID;

/// Thrown when a patient cannot be found by its id (or associated user id).
public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(UUID patientId) {
        super("Patient not found: " + patientId);
    }
}
