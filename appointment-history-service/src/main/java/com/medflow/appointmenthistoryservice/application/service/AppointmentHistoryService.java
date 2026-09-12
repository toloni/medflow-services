package com.medflow.appointmenthistoryservice.application.service;

import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryByAppointmentUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryByPatientUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.RecordAppointmentEventUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.command.RecordAppointmentEventCommand;
import com.medflow.appointmenthistoryservice.application.port.out.AppointmentHistoryRepositoryPort;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/// Implements the appointment history use cases: recording new history entries from
/// domain events, and listing history overall, by appointment, or by patient.
///
/// Acts purely as a read/write facade over [AppointmentHistoryRepositoryPort]; it does not
/// enforce any access control itself.
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentHistoryService implements
        RecordAppointmentEventUseCase,
        ListAppointmentHistoryUseCase,
        ListAppointmentHistoryByAppointmentUseCase,
        ListAppointmentHistoryByPatientUseCase {

    private final AppointmentHistoryRepositoryPort appointmentHistoryRepository;

    @Override
    public void handle(RecordAppointmentEventCommand command) {
        AppointmentHistory history = AppointmentHistory.builder()
                .appointmentId(command.appointmentId())
                .patientId(command.patientId())
                .patientName(command.patientName())
                .doctorId(command.doctorId())
                .doctorName(command.doctorName())
                .scheduledAt(command.scheduledAt())
                .eventType(command.eventType())
                .status(command.status())
                .build();

        appointmentHistoryRepository.save(history);

        log.info("Recorded {} for appointment {}", command.eventType(), command.appointmentId());
    }

    @Override
    public List<AppointmentHistory> list() {
        return appointmentHistoryRepository.findAll();
    }

    @Override
    public List<AppointmentHistory> listByAppointment(UUID appointmentId) {
        return appointmentHistoryRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public List<AppointmentHistory> listByPatient(UUID patientId) {
        return appointmentHistoryRepository.findByPatientId(patientId);
    }
}
