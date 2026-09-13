package com.medflow.appointmenthistoryservice.application.port.in;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;

import java.util.List;
import java.util.UUID;

/// Use case for retrieving all history entries recorded for a given patient.
public interface ListAppointmentHistoryByPatientUseCase {
    List<AppointmentHistory> listByPatient(UUID patientId);
}
