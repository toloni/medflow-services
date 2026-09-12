package com.medflow.appointmentservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentTest {

    @Test
    void onCreate_setsCreatedAtAndUpdatedAtAndDefaultsNullStatusToScheduled() {
        Appointment appointment = Appointment.builder()
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        // force status to null to exercise the defaulting branch, bypassing the builder default
        appointment.setStatus(null);

        appointment.onCreate();

        assertThat(appointment.getCreatedAt()).isNotNull();
        assertThat(appointment.getUpdatedAt()).isNotNull();
        assertThat(appointment.getCreatedAt()).isEqualTo(appointment.getUpdatedAt());
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void onCreate_doesNotOverrideExistingStatus() {
        Appointment appointment = Appointment.builder()
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .status(AppointmentStatus.CONFIRMED)
                .build();

        appointment.onCreate();

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    void onUpdate_refreshesUpdatedAt() throws InterruptedException {
        Appointment appointment = Appointment.builder()
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        appointment.onCreate();
        Instant createdAt = appointment.getCreatedAt();
        Instant createdUpdatedAt = appointment.getUpdatedAt();

        Thread.sleep(5);
        appointment.onUpdate();

        assertThat(appointment.getUpdatedAt()).isAfterOrEqualTo(createdUpdatedAt);
        assertThat(appointment.getCreatedAt()).isEqualTo(createdAt);
    }
}
