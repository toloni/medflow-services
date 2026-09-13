package com.medflow.appointmentservice.adapter.out.messaging;

import com.medflow.appointmentservice.adapter.out.messaging.event.AppointmentEventPayload;
import com.medflow.appointmentservice.application.port.out.AppointmentEventPublisherPort;
import com.medflow.appointmentservice.application.port.out.event.AppointmentEventType;
import com.medflow.appointmentservice.domain.model.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/// Publishes appointment lifecycle events to Kafka, adapting [Appointment]
/// state into an [AppointmentEventPayload] keyed by appointment id.
@Component
@RequiredArgsConstructor
@Slf4j
class KafkaAppointmentEventPublisher implements AppointmentEventPublisherPort {

    private final KafkaTemplate<String, AppointmentEventPayload> kafkaTemplate;

    @Value("${medflow.kafka.topic.appointment-events}")
    private String topic;

    @Override
    public void publish(Appointment appointment, AppointmentEventType eventType) {
        AppointmentEventPayload payload = new AppointmentEventPayload(
                eventType,
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getFullName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getFullName(),
                appointment.getScheduledAt(),
                appointment.getStatus().name()
        );

        kafkaTemplate.send(topic, appointment.getId().toString(), payload);

        log.info("Published {} for appointment {}", eventType, appointment.getId());
    }
}
