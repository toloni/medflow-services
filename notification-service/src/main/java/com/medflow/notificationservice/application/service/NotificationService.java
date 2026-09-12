package com.medflow.notificationservice.application.service;

import com.medflow.notificationservice.application.port.in.ListNotificationsByPatientUseCase;
import com.medflow.notificationservice.application.port.in.ListNotificationsUseCase;
import com.medflow.notificationservice.application.port.in.SendAppointmentReminderUseCase;
import com.medflow.notificationservice.application.port.in.command.AppointmentEventCommand;
import com.medflow.notificationservice.application.port.out.NotificationRepositoryPort;
import com.medflow.notificationservice.application.port.out.ReminderSenderPort;
import com.medflow.notificationservice.domain.model.Notification;
import com.medflow.notificationservice.domain.model.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/// Application service implementing the notification use cases.
///
/// On [#handle(AppointmentEventCommand)], it builds a human-readable reminder message
/// from the appointment event, attempts to send it through the [ReminderSenderPort],
/// and persists the resulting [Notification] — marked [NotificationStatus#SENT] or
/// [NotificationStatus#FAILED] depending on whether sending succeeded — via the
/// [NotificationRepositoryPort]. The message text and date formatting are driven by
/// the `medflow.notification.date-format` and `medflow.notification.date-locale`
/// properties.
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements
        SendAppointmentReminderUseCase,
        ListNotificationsUseCase,
        ListNotificationsByPatientUseCase {

    private static final String CANCELED_STATUS = "CANCELED";

    private final NotificationRepositoryPort notificationRepository;
    private final ReminderSenderPort reminderSender;

    @Value("${medflow.notification.date-format}")
    private String dateFormatPattern;

    @Value("${medflow.notification.date-locale}")
    private String dateLocaleTag;

    /// Builds and sends a reminder for the given appointment event, then records the
    /// outcome as a [Notification] regardless of whether sending succeeded or failed.
    @Override
    public void handle(AppointmentEventCommand command) {
        String message = buildMessage(command);

        Notification notification = Notification.builder()
                .appointmentId(command.appointmentId())
                .patientId(command.patientId())
                .patientName(command.patientName())
                .doctorName(command.doctorName())
                .scheduledAt(command.scheduledAt())
                .eventType(command.eventType())
                .channel("EMAIL")
                .message(message)
                .build();

        try {
            reminderSender.send(command.patientName(), message);
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception ex) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Failed to send reminder for appointment {}", command.appointmentId(), ex);
        }

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> list() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> listByPatient(UUID patientId) {
        return notificationRepository.findByPatientId(patientId);
    }

    /// Renders the reminder text for the event, wording it differently depending on
    /// whether the appointment was created, canceled, or updated to a new time.
    private String buildMessage(AppointmentEventCommand command) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(dateFormatPattern, Locale.forLanguageTag(dateLocaleTag))
                .withZone(ZoneOffset.UTC);
        String when = dateFormatter.format(command.scheduledAt());

        return switch (command.eventType()) {
            case APPOINTMENT_CREATED -> "Hello %s, your appointment with %s has been scheduled for %s."
                    .formatted(command.patientName(), command.doctorName(), when);

            case APPOINTMENT_UPDATED -> CANCELED_STATUS.equalsIgnoreCase(command.status())
                    ? "Hello %s, your appointment with %s scheduled for %s has been canceled."
                    .formatted(command.patientName(), command.doctorName(), when)
                    : "Hello %s, your appointment with %s has been updated. New time: %s."
                    .formatted(command.patientName(), command.doctorName(), when);
        };
    }
}
