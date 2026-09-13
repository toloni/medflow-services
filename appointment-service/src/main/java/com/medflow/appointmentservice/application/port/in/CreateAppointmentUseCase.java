package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.application.port.in.command.CreateAppointmentCommand;
import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.Appointment;

/// Use case for scheduling a new appointment.
public interface CreateAppointmentUseCase {

    /// Creates a new appointment after validating that the doctor and patient exist.
    ///
    /// @param requester authenticated user performing the action
    Appointment create(CreateAppointmentCommand command, AuthenticatedUser requester);
}

