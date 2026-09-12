package com.medflow.appointmentservice.application.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepository;
    @Mock
    private DoctorRepositoryPort doctorRepository;
    @Mock
    private PatientRepositoryPort patientRepository;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private AppointmentEventPublisherPort eventPublisher;

    private AppointmentService appointmentService;

    private User doctorUser;
    private User patientUser;
    private User nurseUser;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository, doctorRepository, patientRepository, userRepository, eventPublisher);

        doctorUser = User.builder()
                .id(UUID.randomUUID())
                .username("doc")
                .passwordHash("hash")
                .role(Role.DOCTOR)
                .fullName("Dr. House")
                .build();

        patientUser = User.builder()
                .id(UUID.randomUUID())
                .username("pat")
                .passwordHash("hash")
                .role(Role.PATIENT)
                .fullName("John Doe")
                .build();

        nurseUser = User.builder()
                .id(UUID.randomUUID())
                .username("nurse")
                .passwordHash("hash")
                .role(Role.NURSE)
                .fullName("Nurse Joy")
                .build();

        doctor = Doctor.builder()
                .id(UUID.randomUUID())
                .user(doctorUser)
                .specialty("Cardiology")
                .licenseNumber("LIC-1")
                .build();

        patient = Patient.builder()
                .id(UUID.randomUUID())
                .user(patientUser)
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .phone("123456")
                .build();
    }

    private Appointment buildAppointment() {
        return Appointment.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .patient(patient)
                .createdBy(nurseUser)
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .status(AppointmentStatus.SCHEDULED)
                .notes("initial notes")
                .build();
    }

    @Nested
    class Create {

        @Test
        void happyPath_buildsAppointmentAndPublishesCreatedEvent() {
            UUID doctorId = doctor.getId();
            UUID patientId = patient.getId();
            Instant scheduledAt = Instant.now().plus(2, ChronoUnit.DAYS);
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);
            CreateAppointmentCommand command = new CreateAppointmentCommand(patientId, doctorId, scheduledAt, "notes here");

            when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
            when(userRepository.findById(requester.userId())).thenReturn(Optional.of(nurseUser));
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Appointment result = appointmentService.create(command, requester);

            assertThat(result.getDoctor()).isEqualTo(doctor);
            assertThat(result.getPatient()).isEqualTo(patient);
            assertThat(result.getCreatedBy()).isEqualTo(nurseUser);
            assertThat(result.getScheduledAt()).isEqualTo(scheduledAt);
            assertThat(result.getNotes()).isEqualTo("notes here");

            ArgumentCaptor<Appointment> savedCaptor = ArgumentCaptor.forClass(Appointment.class);
            verify(appointmentRepository).save(savedCaptor.capture());
            assertThat(savedCaptor.getValue().getDoctor()).isEqualTo(doctor);
            assertThat(savedCaptor.getValue().getPatient()).isEqualTo(patient);
            assertThat(savedCaptor.getValue().getCreatedBy()).isEqualTo(nurseUser);

            verify(eventPublisher).publish(result, AppointmentEventType.APPOINTMENT_CREATED);
        }

        @Test
        void doctorNotFound_throwsDoctorNotFoundException() {
            UUID doctorId = UUID.randomUUID();
            UUID patientId = patient.getId();
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);
            CreateAppointmentCommand command = new CreateAppointmentCommand(
                    patientId, doctorId, Instant.now().plus(1, ChronoUnit.DAYS), "notes");

            when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.create(command, requester))
                    .isInstanceOf(DoctorNotFoundException.class);

            verify(patientRepository, never()).findById(any());
            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publish(any(), any());
        }

        @Test
        void patientNotFound_throwsPatientNotFoundException() {
            UUID doctorId = doctor.getId();
            UUID patientId = UUID.randomUUID();
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);
            CreateAppointmentCommand command = new CreateAppointmentCommand(
                    patientId, doctorId, Instant.now().plus(1, ChronoUnit.DAYS), "notes");

            when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.create(command, requester))
                    .isInstanceOf(PatientNotFoundException.class);

            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publish(any(), any());
        }

        @Test
        void authenticatedUserNotFound_throwsIllegalStateException() {
            UUID doctorId = doctor.getId();
            UUID patientId = patient.getId();
            UUID requesterId = UUID.randomUUID();
            AuthenticatedUser requester = new AuthenticatedUser(requesterId, Role.NURSE);
            CreateAppointmentCommand command = new CreateAppointmentCommand(
                    patientId, doctorId, Instant.now().plus(1, ChronoUnit.DAYS), "notes");

            when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
            when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.create(command, requester))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining(requesterId.toString());

            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publish(any(), any());
        }
    }

    @Nested
    class Update {

        @Test
        void onlyScheduledAtSet_updatesOnlyScheduledAt() {
            Appointment existing = buildAppointment();
            Instant newScheduledAt = Instant.now().plus(5, ChronoUnit.DAYS);
            UpdateAppointmentCommand command = new UpdateAppointmentCommand(newScheduledAt, null, null);
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Appointment result = appointmentService.update(existing.getId(), command, requester);

            assertThat(result.getScheduledAt()).isEqualTo(newScheduledAt);
            assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
            assertThat(result.getNotes()).isEqualTo("initial notes");
            verify(eventPublisher).publish(result, AppointmentEventType.APPOINTMENT_UPDATED);
        }

        @Test
        void onlyStatusSet_updatesOnlyStatus() {
            Appointment existing = buildAppointment();
            Instant originalScheduledAt = existing.getScheduledAt();
            UpdateAppointmentCommand command = new UpdateAppointmentCommand(null, AppointmentStatus.CONFIRMED, null);
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Appointment result = appointmentService.update(existing.getId(), command, requester);

            assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
            assertThat(result.getScheduledAt()).isEqualTo(originalScheduledAt);
            assertThat(result.getNotes()).isEqualTo("initial notes");
            verify(eventPublisher).publish(result, AppointmentEventType.APPOINTMENT_UPDATED);
        }

        @Test
        void onlyNotesSet_updatesOnlyNotes() {
            Appointment existing = buildAppointment();
            Instant originalScheduledAt = existing.getScheduledAt();
            UpdateAppointmentCommand command = new UpdateAppointmentCommand(null, null, "updated notes");
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Appointment result = appointmentService.update(existing.getId(), command, requester);

            assertThat(result.getNotes()).isEqualTo("updated notes");
            assertThat(result.getScheduledAt()).isEqualTo(originalScheduledAt);
            assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
            verify(eventPublisher).publish(result, AppointmentEventType.APPOINTMENT_UPDATED);
        }

        @Test
        void allNull_noFieldChangesButEventStillPublished() {
            Appointment existing = buildAppointment();
            Instant originalScheduledAt = existing.getScheduledAt();
            AppointmentStatus originalStatus = existing.getStatus();
            String originalNotes = existing.getNotes();
            UpdateAppointmentCommand command = new UpdateAppointmentCommand(null, null, null);
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Appointment result = appointmentService.update(existing.getId(), command, requester);

            assertThat(result.getScheduledAt()).isEqualTo(originalScheduledAt);
            assertThat(result.getStatus()).isEqualTo(originalStatus);
            assertThat(result.getNotes()).isEqualTo(originalNotes);
            verify(eventPublisher).publish(result, AppointmentEventType.APPOINTMENT_UPDATED);
        }

        @Test
        void appointmentNotFound_throwsAppointmentNotFoundException() {
            UUID appointmentId = UUID.randomUUID();
            UpdateAppointmentCommand command = new UpdateAppointmentCommand(null, null, "notes");
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.update(appointmentId, command, requester))
                    .isInstanceOf(AppointmentNotFoundException.class);

            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publish(any(), any());
        }
    }

    @Nested
    class Cancel {

        @Test
        void setsStatusToCanceledAndPublishesUpdatedEvent() {
            Appointment existing = buildAppointment();
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(appointmentRepository.save(any(Appointment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Appointment result = appointmentService.cancel(existing.getId(), requester);

            assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELED);
            verify(eventPublisher).publish(result, AppointmentEventType.APPOINTMENT_UPDATED);
        }

        @Test
        void appointmentNotFound_throwsAppointmentNotFoundException() {
            UUID appointmentId = UUID.randomUUID();
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.cancel(appointmentId, requester))
                    .isInstanceOf(AppointmentNotFoundException.class);

            verify(appointmentRepository, never()).save(any());
            verify(eventPublisher, never()).publish(any(), any());
        }
    }

    @Nested
    class GetById {

        @Test
        void nurseIsAlwaysAllowed() {
            Appointment existing = buildAppointment();
            AuthenticatedUser requester = new AuthenticatedUser(UUID.randomUUID(), Role.NURSE);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            Appointment result = appointmentService.getById(existing.getId(), requester);

            assertThat(result).isEqualTo(existing);
        }

        @Test
        void doctorAllowedWhenMatchesAppointmentDoctor() {
            Appointment existing = buildAppointment();
            AuthenticatedUser requester = new AuthenticatedUser(doctorUser.getId(), Role.DOCTOR);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            Appointment result = appointmentService.getById(existing.getId(), requester);

            assertThat(result).isEqualTo(existing);
        }

        @Test
        void doctorMismatch_throwsUnauthorized() {
            Appointment existing = buildAppointment();
            AuthenticatedUser requester = new AuthenticatedUser(UUID.randomUUID(), Role.DOCTOR);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> appointmentService.getById(existing.getId(), requester))
                    .isInstanceOf(UnauthorizedAppointmentAccessException.class);
        }

        @Test
        void patientAllowedWhenMatchesAppointmentPatient() {
            Appointment existing = buildAppointment();
            AuthenticatedUser requester = new AuthenticatedUser(patientUser.getId(), Role.PATIENT);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            Appointment result = appointmentService.getById(existing.getId(), requester);

            assertThat(result).isEqualTo(existing);
        }

        @Test
        void patientMismatch_throwsUnauthorized() {
            Appointment existing = buildAppointment();
            AuthenticatedUser requester = new AuthenticatedUser(UUID.randomUUID(), Role.PATIENT);

            when(appointmentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> appointmentService.getById(existing.getId(), requester))
                    .isInstanceOf(UnauthorizedAppointmentAccessException.class);
        }

        @Test
        void appointmentNotFound_throwsAppointmentNotFoundException() {
            UUID appointmentId = UUID.randomUUID();
            AuthenticatedUser requester = new AuthenticatedUser(UUID.randomUUID(), Role.NURSE);

            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.getById(appointmentId, requester))
                    .isInstanceOf(AppointmentNotFoundException.class);
        }
    }

    @Nested
    class ListAppointments {

        @Test
        void nurse_returnsFindAllResults() {
            AuthenticatedUser requester = new AuthenticatedUser(nurseUser.getId(), Role.NURSE);
            List<Appointment> expected = List.of(buildAppointment(), buildAppointment());

            when(appointmentRepository.findAll()).thenReturn(expected);

            List<Appointment> result = appointmentService.list(requester);

            assertThat(result).isEqualTo(expected);
            verify(appointmentRepository).findAll();
            verify(appointmentRepository, never()).findByDoctorId(any());
            verify(appointmentRepository, never()).findByPatientId(any());
        }

        @Test
        void doctor_looksUpDoctorThenFindsByDoctorId() {
            AuthenticatedUser requester = new AuthenticatedUser(doctorUser.getId(), Role.DOCTOR);
            List<Appointment> expected = List.of(buildAppointment());

            when(doctorRepository.findByUserId(doctorUser.getId())).thenReturn(Optional.of(doctor));
            when(appointmentRepository.findByDoctorId(doctor.getId())).thenReturn(expected);

            List<Appointment> result = appointmentService.list(requester);

            assertThat(result).isEqualTo(expected);
            verify(appointmentRepository).findByDoctorId(doctor.getId());
        }

        @Test
        void doctor_notFound_throwsDoctorNotFoundException() {
            UUID requesterId = UUID.randomUUID();
            AuthenticatedUser requester = new AuthenticatedUser(requesterId, Role.DOCTOR);

            when(doctorRepository.findByUserId(requesterId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.list(requester))
                    .isInstanceOf(DoctorNotFoundException.class);

            verify(appointmentRepository, never()).findByDoctorId(any());
        }

        @Test
        void patient_looksUpPatientThenFindsByPatientId() {
            AuthenticatedUser requester = new AuthenticatedUser(patientUser.getId(), Role.PATIENT);
            List<Appointment> expected = List.of(buildAppointment());

            when(patientRepository.findByUserId(patientUser.getId())).thenReturn(Optional.of(patient));
            when(appointmentRepository.findByPatientId(patient.getId())).thenReturn(expected);

            List<Appointment> result = appointmentService.list(requester);

            assertThat(result).isEqualTo(expected);
            verify(appointmentRepository).findByPatientId(patient.getId());
        }

        @Test
        void patient_notFound_throwsPatientNotFoundException() {
            UUID requesterId = UUID.randomUUID();
            AuthenticatedUser requester = new AuthenticatedUser(requesterId, Role.PATIENT);

            when(patientRepository.findByUserId(requesterId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.list(requester))
                    .isInstanceOf(PatientNotFoundException.class);

            verify(appointmentRepository, never()).findByPatientId(any());
        }
    }
}
