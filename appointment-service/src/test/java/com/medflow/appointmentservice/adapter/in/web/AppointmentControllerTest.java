package com.medflow.appointmentservice.adapter.in.web;

import com.medflow.appointmentservice.adapter.in.security.AppUserDetails;
import com.medflow.appointmentservice.adapter.in.web.dto.AppointmentResponse;
import com.medflow.appointmentservice.adapter.in.web.dto.CreateAppointmentRequest;
import com.medflow.appointmentservice.adapter.in.web.dto.UpdateAppointmentRequest;
import com.medflow.appointmentservice.application.port.in.*;
import com.medflow.appointmentservice.application.port.in.command.CreateAppointmentCommand;
import com.medflow.appointmentservice.application.port.in.command.UpdateAppointmentCommand;
import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private CreateAppointmentUseCase createAppointmentUseCase;
    @Mock
    private UpdateAppointmentUseCase updateAppointmentUseCase;
    @Mock
    private CancelAppointmentUseCase cancelAppointmentUseCase;
    @Mock
    private GetAppointmentUseCase getAppointmentUseCase;
    @Mock
    private ListAppointmentsUseCase listAppointmentsUseCase;

    @Mock
    private AppUserDetails principal;

    private AppointmentController controller;

    private User doctorUser;
    private User patientUser;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        controller = new AppointmentController(
                createAppointmentUseCase,
                updateAppointmentUseCase,
                cancelAppointmentUseCase,
                getAppointmentUseCase,
                listAppointmentsUseCase);

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

    private Appointment buildAppointment(UUID id) {
        return Appointment.builder()
                .id(id)
                .doctor(doctor)
                .patient(patient)
                .createdBy(doctorUser)
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .status(AppointmentStatus.SCHEDULED)
                .notes("notes")
                .build();
    }

    @Test
    void create_delegatesToUseCaseAndReturns201WithLocation() {
        UUID requesterId = UUID.randomUUID();
        when(principal.getUserId()).thenReturn(requesterId);
        when(principal.getRole()).thenReturn(Role.NURSE);

        UUID patientId = patient.getId();
        UUID doctorId = doctor.getId();
        Instant scheduledAt = Instant.now().plus(2, ChronoUnit.DAYS);
        CreateAppointmentRequest request = new CreateAppointmentRequest(patientId, doctorId, scheduledAt, "some notes");

        UUID appointmentId = UUID.randomUUID();
        Appointment createdAppointment = buildAppointment(appointmentId);
        when(createAppointmentUseCase.create(any(CreateAppointmentCommand.class), any(AuthenticatedUser.class)))
                .thenReturn(createdAppointment);

        ResponseEntity<AppointmentResponse> response = controller.create(request, principal);

        ArgumentCaptor<CreateAppointmentCommand> commandCaptor = ArgumentCaptor.forClass(CreateAppointmentCommand.class);
        ArgumentCaptor<AuthenticatedUser> requesterCaptor = ArgumentCaptor.forClass(AuthenticatedUser.class);
        verify(createAppointmentUseCase).create(commandCaptor.capture(), requesterCaptor.capture());

        assertThat(commandCaptor.getValue().patientId()).isEqualTo(patientId);
        assertThat(commandCaptor.getValue().doctorId()).isEqualTo(doctorId);
        assertThat(commandCaptor.getValue().scheduledAt()).isEqualTo(scheduledAt);
        assertThat(commandCaptor.getValue().notes()).isEqualTo("some notes");

        assertThat(requesterCaptor.getValue().userId()).isEqualTo(requesterId);
        assertThat(requesterCaptor.getValue().role()).isEqualTo(Role.NURSE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(
                java.net.URI.create("/api/appointments/" + appointmentId));
        assertThat(response.getBody()).isEqualTo(AppointmentResponse.from(createdAppointment));
    }

    @Test
    void update_delegatesToUseCaseAndReturns200() {
        UUID requesterId = UUID.randomUUID();
        when(principal.getUserId()).thenReturn(requesterId);
        when(principal.getRole()).thenReturn(Role.DOCTOR);

        UUID appointmentId = UUID.randomUUID();
        Instant scheduledAt = Instant.now().plus(3, ChronoUnit.DAYS);
        UpdateAppointmentRequest request = new UpdateAppointmentRequest(scheduledAt, AppointmentStatus.CONFIRMED, "updated notes");

        Appointment updatedAppointment = buildAppointment(appointmentId);
        when(updateAppointmentUseCase.update(eq(appointmentId), any(UpdateAppointmentCommand.class), any(AuthenticatedUser.class)))
                .thenReturn(updatedAppointment);

        ResponseEntity<AppointmentResponse> response = controller.update(appointmentId, request, principal);

        ArgumentCaptor<UpdateAppointmentCommand> commandCaptor = ArgumentCaptor.forClass(UpdateAppointmentCommand.class);
        verify(updateAppointmentUseCase).update(eq(appointmentId), commandCaptor.capture(), any(AuthenticatedUser.class));

        assertThat(commandCaptor.getValue().scheduledAt()).isEqualTo(scheduledAt);
        assertThat(commandCaptor.getValue().status()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(commandCaptor.getValue().notes()).isEqualTo("updated notes");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(AppointmentResponse.from(updatedAppointment));
    }

    @Test
    void cancel_delegatesToUseCaseAndReturns200() {
        UUID requesterId = UUID.randomUUID();
        when(principal.getUserId()).thenReturn(requesterId);
        when(principal.getRole()).thenReturn(Role.NURSE);

        UUID appointmentId = UUID.randomUUID();
        Appointment canceledAppointment = buildAppointment(appointmentId);
        canceledAppointment.setStatus(AppointmentStatus.CANCELED);
        when(cancelAppointmentUseCase.cancel(eq(appointmentId), any(AuthenticatedUser.class)))
                .thenReturn(canceledAppointment);

        ResponseEntity<AppointmentResponse> response = controller.cancel(appointmentId, principal);

        verify(cancelAppointmentUseCase).cancel(eq(appointmentId), any(AuthenticatedUser.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(AppointmentResponse.from(canceledAppointment));
    }

    @Test
    void getById_delegatesToUseCaseAndReturns200() {
        UUID requesterId = UUID.randomUUID();
        when(principal.getUserId()).thenReturn(requesterId);
        when(principal.getRole()).thenReturn(Role.PATIENT);

        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = buildAppointment(appointmentId);
        when(getAppointmentUseCase.getById(eq(appointmentId), any(AuthenticatedUser.class)))
                .thenReturn(appointment);

        ResponseEntity<AppointmentResponse> response = controller.getById(appointmentId, principal);

        ArgumentCaptor<AuthenticatedUser> requesterCaptor = ArgumentCaptor.forClass(AuthenticatedUser.class);
        verify(getAppointmentUseCase).getById(eq(appointmentId), requesterCaptor.capture());
        assertThat(requesterCaptor.getValue().userId()).isEqualTo(requesterId);
        assertThat(requesterCaptor.getValue().role()).isEqualTo(Role.PATIENT);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(AppointmentResponse.from(appointment));
    }

    @Test
    void list_delegatesToUseCaseAndMapsAllResults() {
        UUID requesterId = UUID.randomUUID();
        when(principal.getUserId()).thenReturn(requesterId);
        when(principal.getRole()).thenReturn(Role.NURSE);

        Appointment first = buildAppointment(UUID.randomUUID());
        Appointment second = buildAppointment(UUID.randomUUID());
        when(listAppointmentsUseCase.list(any(AuthenticatedUser.class))).thenReturn(List.of(first, second));

        ResponseEntity<List<AppointmentResponse>> response = controller.list(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(
                AppointmentResponse.from(first), AppointmentResponse.from(second));
    }
}
