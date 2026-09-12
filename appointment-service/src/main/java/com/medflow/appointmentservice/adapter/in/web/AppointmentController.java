package com.medflow.appointmentservice.adapter.in.web;

import com.medflow.appointmentservice.adapter.in.security.AppUserDetails;
import com.medflow.appointmentservice.adapter.in.web.dto.AppointmentResponse;
import com.medflow.appointmentservice.adapter.in.web.dto.CreateAppointmentRequest;
import com.medflow.appointmentservice.adapter.in.web.dto.UpdateAppointmentRequest;
import com.medflow.appointmentservice.application.port.in.*;
import com.medflow.appointmentservice.application.port.in.command.CreateAppointmentCommand;
import com.medflow.appointmentservice.application.port.in.command.UpdateAppointmentCommand;
import com.medflow.appointmentservice.application.shared.AuthenticatedUser;
import com.medflow.appointmentservice.domain.model.Appointment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/// REST controller exposing CRUD and lifecycle operations for appointments
/// under `/api/appointments`: create, update, cancel, retrieve by id and list.
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final GetAppointmentUseCase getAppointmentUseCase;
    private final ListAppointmentsUseCase listAppointmentsUseCase;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody CreateAppointmentRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {

        Appointment appointment = createAppointmentUseCase.create(
                new CreateAppointmentCommand(
                        request.patientId(),
                        request.doctorId(),
                        request.scheduledAt(),
                        request.notes()
                ),
                toAuthenticatedUser(principal)
        );

        return ResponseEntity
                .created(URI.create("/api/appointments/" + appointment.getId()))
                .body(AppointmentResponse.from(appointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {

        Appointment appointment = updateAppointmentUseCase.update(
                id,
                new UpdateAppointmentCommand(
                        request.scheduledAt(),
                        request.status(),
                        request.notes()
                ),
                toAuthenticatedUser(principal)
        );

        return ResponseEntity.ok(AppointmentResponse.from(appointment));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal AppUserDetails principal) {

        Appointment appointment = cancelAppointmentUseCase.cancel(id, toAuthenticatedUser(principal));
        return ResponseEntity.ok(AppointmentResponse.from(appointment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AppUserDetails principal) {

        Appointment appointment = getAppointmentUseCase.getById(id, toAuthenticatedUser(principal));
        return ResponseEntity.ok(AppointmentResponse.from(appointment));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> list(
            @AuthenticationPrincipal AppUserDetails principal) {

        List<AppointmentResponse> response = listAppointmentsUseCase
                .list(toAuthenticatedUser(principal))
                .stream()
                .map(AppointmentResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    private AuthenticatedUser toAuthenticatedUser(AppUserDetails principal) {
        return new AuthenticatedUser(principal.getUserId(), principal.getRole());
    }
}
