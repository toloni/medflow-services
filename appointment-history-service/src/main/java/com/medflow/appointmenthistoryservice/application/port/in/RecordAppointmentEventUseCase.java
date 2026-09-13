package com.medflow.appointmenthistoryservice.application.port.in;

import com.medflow.appointmenthistoryservice.application.port.in.command.RecordAppointmentEventCommand;

/// Use case for recording a new appointment history entry from an incoming domain event.
public interface RecordAppointmentEventUseCase {
    void handle(RecordAppointmentEventCommand command);
}
