package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.Appointment;

import java.util.UUID;

/// Use case for canceling an existing appointment.
public interface CancelAppointmentUseCase {

    /// Cancels the appointment identified by `appointmentId`.
    ///
    /// @param requester authenticated user performing the action
    Appointment cancel(UUID appointmentId, AuthenticatedUser requester);
}
