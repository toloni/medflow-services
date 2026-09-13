package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.application.port.in.command.UpdateAppointmentCommand;
import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.Appointment;

import java.util.UUID;

/// Use case for partially updating an existing appointment.
public interface UpdateAppointmentUseCase {

    /// Applies the non-null fields of `command` to the appointment identified
    /// by `appointmentId`.
    Appointment update(UUID appointmentId, UpdateAppointmentCommand command, AuthenticatedUser requester);
}
