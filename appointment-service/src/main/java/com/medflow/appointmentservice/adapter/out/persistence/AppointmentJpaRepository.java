package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.domain.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [Appointment], eagerly fetching patient
/// and doctor user data via entity graphs to avoid lazy-loading issues.
interface AppointmentJpaRepository extends JpaRepository<Appointment, UUID> {

    @Override
    @EntityGraph(attributePaths = {"patient.user", "doctor.user"})
    Optional<Appointment> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"patient.user", "doctor.user"})
    List<Appointment> findAll();

    @EntityGraph(attributePaths = {"patient.user", "doctor.user"})
    List<Appointment> findByDoctorId(UUID doctorId);

    @EntityGraph(attributePaths = {"patient.user", "doctor.user"})
    List<Appointment> findByPatientId(UUID patientId);
}
