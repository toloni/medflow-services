package com.medflow.appointmentservice.domain.exception;

import java.util.UUID;

/// Thrown when an appointment cannot be found by its id.
public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(UUID appointmentId) {
        super("Appointment not found: " + appointmentId);
    }
}
