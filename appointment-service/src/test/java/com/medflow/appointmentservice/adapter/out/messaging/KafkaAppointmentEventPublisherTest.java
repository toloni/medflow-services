package com.medflow.appointmentservice.adapter.out.messaging;

import com.medflow.appointmentservice.adapter.out.messaging.event.AppointmentEventPayload;
import com.medflow.appointmentservice.application.port.out.event.AppointmentEventType;
import com.medflow.appointmentservice.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaAppointmentEventPublisherTest {

    private static final String TOPIC = "appointment-events-topic";

    @Mock
    private KafkaTemplate<String, AppointmentEventPayload> kafkaTemplate;

    private KafkaAppointmentEventPublisher publisher;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        publisher = new KafkaAppointmentEventPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "topic", TOPIC);

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

        appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .patient(patient)
                .createdBy(doctorUser)
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .status(AppointmentStatus.SCHEDULED)
                .notes("notes")
                .build();
    }

    @Test
    void publish_sendsCorrectlyBuiltPayloadToConfiguredTopic() {
        publisher.publish(appointment, AppointmentEventType.APPOINTMENT_CREATED);

        ArgumentCaptor<AppointmentEventPayload> payloadCaptor = ArgumentCaptor.forClass(AppointmentEventPayload.class);
        verify(kafkaTemplate).send(eq(TOPIC), eq(appointment.getId().toString()), payloadCaptor.capture());

        AppointmentEventPayload payload = payloadCaptor.getValue();
        assertThat(payload.eventType()).isEqualTo(AppointmentEventType.APPOINTMENT_CREATED);
        assertThat(payload.appointmentId()).isEqualTo(appointment.getId());
        assertThat(payload.patientId()).isEqualTo(appointment.getPatient().getId());
        assertThat(payload.patientName()).isEqualTo(appointment.getPatient().getUser().getFullName());
        assertThat(payload.doctorId()).isEqualTo(appointment.getDoctor().getId());
        assertThat(payload.doctorName()).isEqualTo(appointment.getDoctor().getUser().getFullName());
        assertThat(payload.scheduledAt()).isEqualTo(appointment.getScheduledAt());
        assertThat(payload.status()).isEqualTo(appointment.getStatus().name());
    }
}
