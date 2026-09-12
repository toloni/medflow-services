package com.medflow.appointmenthistoryservice.adapter.in.messaging;

import com.medflow.appointmenthistoryservice.adapter.in.messaging.event.AppointmentEventPayload;
import com.medflow.appointmenthistoryservice.application.port.in.RecordAppointmentEventUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.command.RecordAppointmentEventCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/// Consumes appointment lifecycle events from the configured Kafka topic and forwards
/// each one to [RecordAppointmentEventUseCase] to be persisted as a history entry.
@Component
@RequiredArgsConstructor
@Slf4j
class AppointmentEventListener {

    private final RecordAppointmentEventUseCase recordAppointmentEventUseCase;

    @KafkaListener(
            topics = "${medflow.kafka.topic.appointment-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onAppointmentEvent(AppointmentEventPayload payload) {
        log.info("Received {} for appointment {}", payload.eventType(), payload.appointmentId());

        recordAppointmentEventUseCase.handle(new RecordAppointmentEventCommand(
                payload.eventType(),
                payload.appointmentId(),
                payload.patientId(),
                payload.patientName(),
                payload.doctorId(),
                payload.doctorName(),
                payload.scheduledAt(),
                payload.status()
        ));
    }
}
