package com.medflow.appointmentservice.domain.exception;

import java.util.UUID;

/// Thrown when a doctor cannot be found by its id (or associated user id).
public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(UUID doctorId) {
        super("Doctor not found: " + doctorId);
    }
}