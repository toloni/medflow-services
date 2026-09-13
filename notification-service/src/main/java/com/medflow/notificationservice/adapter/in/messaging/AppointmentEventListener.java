package com.medflow.notificationservice.adapter.in.messaging;

import com.medflow.notificationservice.adapter.in.messaging.event.AppointmentEventPayload;
import com.medflow.notificationservice.application.port.in.SendAppointmentReminderUseCase;
import com.medflow.notificationservice.application.port.in.command.AppointmentEventCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/// Consumes appointment domain events from the Kafka topic configured in
/// `medflow.kafka.topic.appointment-events` and triggers reminder notifications.
@Component
@RequiredArgsConstructor
@Slf4j
class AppointmentEventListener {

    private final SendAppointmentReminderUseCase sendAppointmentReminderUseCase;

    /// Maps an incoming Kafka payload to an [AppointmentEventCommand] and delegates
    /// it to [SendAppointmentReminderUseCase] for processing.
    @KafkaListener(
            topics = "${medflow.kafka.topic.appointment-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onAppointmentEvent(AppointmentEventPayload payload) {
        log.info("Received {} for appointment {}", payload.eventType(), payload.appointmentId());

        sendAppointmentReminderUseCase.handle(new AppointmentEventCommand(
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
