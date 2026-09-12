package com.medflow.appointmenthistoryservice.application.port.in;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;

import java.util.List;

/// Use case for retrieving the full appointment history.
public interface ListAppointmentHistoryUseCase {
    List<AppointmentHistory> list();
}
