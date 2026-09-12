package com.medflow.appointmenthistoryservice.adapter.out.persistence;

import com.medflow.appointmenthistoryservice.application.port.out.AppointmentHistoryRepositoryPort;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/// Adapts [AppointmentHistoryRepositoryPort] to the underlying Spring Data JPA repository.
@Component
@RequiredArgsConstructor
class AppointmentHistoryRepositoryAdapter implements AppointmentHistoryRepositoryPort {

    private final AppointmentHistoryJpaRepository appointmentHistoryJpaRepository;

    @Override
    public AppointmentHistory save(AppointmentHistory appointmentHistory) {
        return appointmentHistoryJpaRepository.save(appointmentHistory);
    }

    @Override
    public List<AppointmentHistory> findAll() {
        return appointmentHistoryJpaRepository.findAllByOrderByRecordedAtDesc();
    }

    @Override
    public List<AppointmentHistory> findByAppointmentId(UUID appointmentId) {
        return appointmentHistoryJpaRepository.findByAppointmentIdOrderByRecordedAtAsc(appointmentId);
    }

    @Override
    public List<AppointmentHistory> findByPatientId(UUID patientId) {
        return appointmentHistoryJpaRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }
}
