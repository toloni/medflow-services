package com.medflow.notificationservice.application.port.in;

import com.medflow.notificationservice.application.port.in.command.AppointmentEventCommand;

/// Use case for reacting to an appointment domain event by sending and
/// recording a reminder notification.
public interface SendAppointmentReminderUseCase {
    void handle(AppointmentEventCommand command);
}
