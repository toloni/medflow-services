package com.medflow.appointmentservice.application.port.out;

import com.medflow.appointmentservice.domain.model.Appointment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// Output port for persisting and querying [Appointment] aggregates.
public interface AppointmentRepositoryPort {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(UUID appointmentId);

    List<Appointment> findAll();

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByPatientId(UUID patientId);
}
