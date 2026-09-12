package com.medflow.appointmenthistoryservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentHistoryTest {

    @Test
    void onCreateSetsRecordedAt() {
        AppointmentHistory history = AppointmentHistory.builder()
                .patientName("Jane Doe")
                .doctorName("Dr. Smith")
                .status("SCHEDULED")
                .eventType(AppointmentEventType.APPOINTMENT_CREATED)
                .build();

        assertThat(history.getRecordedAt()).isNull();

        Instant before = Instant.now();
        history.onCreate();
        Instant after = Instant.now();

        assertThat(history.getRecordedAt()).isNotNull();
        assertThat(history.getRecordedAt()).isBetween(before, after);
    }
}
