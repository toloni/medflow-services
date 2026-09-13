package com.medflow.appointmenthistoryservice.adapter.in.graphql.dto;

import com.medflow.appointmenthistoryservice.domain.model.AppointmentEventType;
import com.medflow.appointmenthistoryservice.domain.model.AppointmentHistory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentHistoryEntryTest {

    @Test
    void fromMapsEveryFieldFromAppointmentHistory() {
        UUID id = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        Instant scheduledAt = Instant.parse("2026-04-10T12:00:00Z");
        Instant recordedAt = Instant.parse("2026-04-10T12:01:00Z");

        AppointmentHistory history = AppointmentHistory.builder()
                .id(id)
                .appointmentId(appointmentId)
                .patientId(patientId)
                .patientName("Jane Doe")
                .doctorId(doctorId)
                .doctorName("Dr. Smith")
                .scheduledAt(scheduledAt)
                .eventType(AppointmentEventType.APPOINTMENT_UPDATED)
                .status("CONFIRMED")
                .recordedAt(recordedAt)
                .build();

        AppointmentHistoryEntry entry = AppointmentHistoryEntry.from(history);

        assertThat(entry.id()).isEqualTo(id);
        assertThat(entry.appointmentId()).isEqualTo(appointmentId);
        assertThat(entry.patientId()).isEqualTo(patientId);
        assertThat(entry.patientName()).isEqualTo("Jane Doe");
        assertThat(entry.doctorId()).isEqualTo(doctorId);
        assertThat(entry.doctorName()).isEqualTo("Dr. Smith");
        assertThat(entry.scheduledAt()).isEqualTo(scheduledAt);
        assertThat(entry.eventType()).isEqualTo(AppointmentEventType.APPOINTMENT_UPDATED);
        assertThat(entry.status()).isEqualTo("CONFIRMED");
        assertThat(entry.recordedAt()).isEqualTo(recordedAt);
    }
}
