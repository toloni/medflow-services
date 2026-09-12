package com.medflow.appointmentservice.domain.exception;

/// Thrown when a doctor or patient attempts to access an appointment that
/// does not involve them.
public class UnauthorizedAppointmentAccessException extends RuntimeException {

    public UnauthorizedAppointmentAccessException() {
        super("You are not allowed to access this appointment");
    }
}
