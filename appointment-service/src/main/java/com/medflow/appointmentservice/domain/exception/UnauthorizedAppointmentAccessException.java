package com.medflow.appointmentservice.domain.exception;

public class UnauthorizedAppointmentAccessException extends RuntimeException {
  public UnauthorizedAppointmentAccessException(String message) {
    super(message);
  }
}
