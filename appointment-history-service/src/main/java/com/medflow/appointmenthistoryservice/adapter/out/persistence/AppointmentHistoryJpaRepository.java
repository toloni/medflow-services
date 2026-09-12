package com.medflow.appointmenthistoryservice.adapter.out.persistence;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/// Spring Data JPA repository for [AppointmentHistory] records.
interface AppointmentHistoryJpaRepository extends JpaRepository<AppointmentHistory, UUID> {
    List<AppointmentHistory> findAllByOrderByRecordedAtDesc();

    List<AppointmentHistory> findByAppointmentIdOrderByRecordedAtAsc(UUID appointmentId);

    List<AppointmentHistory> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
