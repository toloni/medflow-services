package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.Appointment;

import java.util.List;

/// Use case for listing appointments visible to the requester.
public interface ListAppointmentsUseCase {

    /// Returns the appointments visible to `requester`, scoped by their role
    /// (nurses see all, doctors and patients only their own).
    List<Appointment> list(AuthenticatedUser requester);
}
