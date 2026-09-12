package com.medflow.appointmenthistoryservice.application.port.in;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;

import java.util.List;
import java.util.UUID;

/// Use case for retrieving the history entries recorded for a single appointment.
public interface ListAppointmentHistoryByAppointmentUseCase {
    List<AppointmentHistory> listByAppointment(UUID appointmentId);
}
