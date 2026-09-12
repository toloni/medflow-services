package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.Appointment;

import java.util.UUID;

/// Use case for retrieving a single appointment by id.
public interface GetAppointmentUseCase {

    /// Returns the appointment identified by `appointmentId`.
    ///
    /// @throws com.medflow.appointmentservice.domain.exception.UnauthorizedAppointmentAccessException
    ///         if `requester` is not allowed to view this appointment
    Appointment getById(UUID appointmentId, AuthenticatedUser requester);
}
