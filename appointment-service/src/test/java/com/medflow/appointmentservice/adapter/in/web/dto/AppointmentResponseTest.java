package com.medflow.appointmentservice.adapter.in.web.dto;

import com.medflow.appointmentservice.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentResponseTest {

    @Test
    void from_mapsAllFieldsFromAppointment() {
        User doctorUser = User.builder()
                .id(UUID.randomUUID())
                .username("doc")
                .passwordHash("hash")
                .role(Role.DOCTOR)
                .fullName("Dr. House")
                .build();

        User patientUser = User.builder()
                .id(UUID.randomUUID())
                .username("pat")
                .passwordHash("hash")
                .role(Role.PATIENT)
                .fullName("John Doe")
                .build();

        Doctor doctor = Doctor.builder()
                .id(UUID.randomUUID())
                .user(doctorUser)
                .specialty("Cardiology")
                .licenseNumber("LIC-1")
                .build();

        Patient patient = Patient.builder()
                .id(UUID.randomUUID())
                .user(patientUser)
                .birthDate(LocalDate.of(1990, 1, 1))
                .phone("123456")
                .build();

        Instant scheduledAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant updatedAt = Instant.now();

        Appointment appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .patient(patient)
                .createdBy(doctorUser)
                .scheduledAt(scheduledAt)
                .status(AppointmentStatus.CONFIRMED)
                .notes("some notes")
                .build();
        appointment.setCreatedAt(createdAt);
        appointment.setUpdatedAt(updatedAt);

        AppointmentResponse response = AppointmentResponse.from(appointment);

        assertThat(response.id()).isEqualTo(appointment.getId());
        assertThat(response.patientId()).isEqualTo(patient.getId());
        assertThat(response.patientName()).isEqualTo("John Doe");
        assertThat(response.doctorId()).isEqualTo(doctor.getId());
        assertThat(response.doctorName()).isEqualTo("Dr. House");
        assertThat(response.scheduledAt()).isEqualTo(scheduledAt);
        assertThat(response.status()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(response.notes()).isEqualTo("some notes");
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(updatedAt);
    }
}
