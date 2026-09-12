package com.medflow.appointmenthistoryservice.application.port.out;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;

import java.util.List;
import java.util.UUID;

/// Output port for persisting and querying [AppointmentHistory] records, independent of
/// the underlying storage technology.
public interface AppointmentHistoryRepositoryPort {
    AppointmentHistory save(AppointmentHistory appointmentHistory);

    List<AppointmentHistory> findAll();

    List<AppointmentHistory> findByAppointmentId(UUID appointmentId);

    List<AppointmentHistory> findByPatientId(UUID patientId);
}
