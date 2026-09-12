package com.medflow.appointmentservice.application.service;

import com.medflow.appointmentservice.application.port.in.*;
import com.medflow.appointmentservice.application.port.in.command.CreateAppointmentCommand;
import com.medflow.appointmentservice.application.port.in.command.UpdateAppointmentCommand;
import com.medflow.appointmentservice.application.port.out.*;
import com.medflow.appointmentservice.application.port.out.event.AppointmentEventType;
import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.exception.AppointmentNotFoundException;
import com.medflow.appointmentservice.domain.exception.DoctorNotFoundException;
import com.medflow.appointmentservice.domain.exception.PatientNotFoundException;
import com.medflow.appointmentservice.domain.exception.UnauthorizedAppointmentAccessException;
import com.medflow.appointmentservice.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/// Handles appointment lifecycle use cases: creation, update, cancellation,
/// retrieval and listing.
///
/// Enforces role-based access: nurses see and access every appointment,
/// while doctors and patients are restricted to appointments involving
/// themselves. Creation and update also publish domain events via
/// [AppointmentEventPublisherPort].
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService implements
        CreateAppointmentUseCase,
        UpdateAppointmentUseCase,
        CancelAppointmentUseCase,
        GetAppointmentUseCase,
        ListAppointmentsUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private final DoctorRepositoryPort doctorRepository;
    private final PatientRepositoryPort patientRepository;
    private final UserRepositoryPort userRepository;
    private final AppointmentEventPublisherPort eventPublisher;

    @Override
    public Appointment create(CreateAppointmentCommand command, AuthenticatedUser requester) {
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(command.doctorId()));

        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> new PatientNotFoundException(command.patientId()));

        User createdBy = userRepository.findById(requester.userId())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + requester.userId()));

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .createdBy(createdBy)
                .scheduledAt(command.scheduledAt())
                .notes(command.notes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        eventPublisher.publish(saved, AppointmentEventType.APPOINTMENT_CREATED);
        return saved;
    }

    @Override
    public Appointment update(UUID appointmentId, UpdateAppointmentCommand command, AuthenticatedUser requester) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        if (command.scheduledAt() != null) {
            appointment.setScheduledAt(command.scheduledAt());
        }
        if (command.status() != null) {
            appointment.setStatus(command.status());
        }
        if (command.notes() != null) {
            appointment.setNotes(command.notes());
        }

        Appointment saved = appointmentRepository.save(appointment);
        eventPublisher.publish(saved, AppointmentEventType.APPOINTMENT_UPDATED);
        return saved;
    }

    @Override
    public Appointment cancel(UUID appointmentId, AuthenticatedUser requester) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        appointment.setStatus(com.medflow.appointmentservice.domain.model.AppointmentStatus.CANCELED);

        Appointment saved = appointmentRepository.save(appointment);
        eventPublisher.publish(saved, AppointmentEventType.APPOINTMENT_UPDATED);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getById(UUID appointmentId, AuthenticatedUser requester) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        assertCanAccess(appointment, requester);
        return appointment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> list(AuthenticatedUser requester) {
        return switch (requester.role()) {
            case NURSE -> appointmentRepository.findAll();

            case DOCTOR -> {
                Doctor doctor = doctorRepository.findByUserId(requester.userId())
                        .orElseThrow(() -> new DoctorNotFoundException(requester.userId()));
                yield appointmentRepository.findByDoctorId(doctor.getId());
            }

            case PATIENT -> {
                Patient patient = patientRepository.findByUserId(requester.userId())
                        .orElseThrow(() -> new PatientNotFoundException(requester.userId()));
                yield appointmentRepository.findByPatientId(patient.getId());
            }
        };
    }

    /// Verifies that `requester` is allowed to view `appointment`: nurses
    /// always may, doctors and patients only when the appointment involves them.
    ///
    /// @throws UnauthorizedAppointmentAccessException if access is not allowed
    private void assertCanAccess(Appointment appointment, AuthenticatedUser requester) {
        if (requester.role() == Role.NURSE) {
            return;
        }

        if (requester.role() == Role.DOCTOR
                && appointment.getDoctor().getUser().getId().equals(requester.userId())) {
            return;
        }

        if (requester.role() == Role.PATIENT
                && appointment.getPatient().getUser().getId().equals(requester.userId())) {
            return;
        }

        throw new UnauthorizedAppointmentAccessException();
    }
}