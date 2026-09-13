package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.application.port.out.AppointmentRepositoryPort;
import com.medflow.appointmentservice.domain.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/// Adapts [AppointmentRepositoryPort] to Spring Data JPA via [AppointmentJpaRepository].
@Component
@RequiredArgsConstructor
class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final AppointmentJpaRepository jpaRepository;

    @Override
    public Appointment save(Appointment appointment) {
        return jpaRepository.saveAndFlush(appointment);
    }

    @Override
    public Optional<Appointment> findById(UUID appointmentId) {
        return jpaRepository.findById(appointmentId);
    }

    @Override
    public List<Appointment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Appointment> findByDoctorId(UUID doctorId) {
        return jpaRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId);
    }
}
