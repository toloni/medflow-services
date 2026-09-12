package com.medflow.notificationservice.application.service;

import com.medflow.notificationservice.application.port.in.command.AppointmentEventCommand;
import com.medflow.notificationservice.application.port.out.NotificationRepositoryPort;
import com.medflow.notificationservice.application.port.out.ReminderSenderPort;
import com.medflow.notificationservice.domain.model.AppointmentEventType;
import com.medflow.notificationservice.domain.model.Notification;
import com.medflow.notificationservice.domain.model.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final Instant SCHEDULED_AT = Instant.parse("2026-01-15T14:30:00Z");
    // dd/MM/yyyy HH:mm, pt-BR, UTC -> "15/01/2026 14:30"
    private static final String FORMATTED_DATE = "15/01/2026 14:30";

    @Mock
    private NotificationRepositoryPort notificationRepository;

    @Mock
    private ReminderSenderPort reminderSender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, reminderSender);
        ReflectionTestUtils.setField(notificationService, "dateFormatPattern", "dd/MM/yyyy HH:mm");
        ReflectionTestUtils.setField(notificationService, "dateLocaleTag", "pt-BR");
    }

    private AppointmentEventCommand command(AppointmentEventType eventType, String status) {
        return new AppointmentEventCommand(
                eventType,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Jane Doe",
                UUID.randomUUID(),
                "Dr. House",
                SCHEDULED_AT,
                status
        );
    }

    @Test
    void handle_whenReminderSentSuccessfully_savesNotificationAsSentForAppointmentCreated() {
        AppointmentEventCommand command = command(AppointmentEventType.APPOINTMENT_CREATED, null);

        notificationService.handle(command);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(reminderSender).send(command.patientName(), "Hello Jane Doe, your appointment with Dr. House has been scheduled for " + FORMATTED_DATE + ".");
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(saved.getChannel()).isEqualTo("EMAIL");
        assertThat(saved.getMessage()).isEqualTo("Hello Jane Doe, your appointment with Dr. House has been scheduled for " + FORMATTED_DATE + ".");
        assertThat(saved.getAppointmentId()).isEqualTo(command.appointmentId());
        assertThat(saved.getPatientId()).isEqualTo(command.patientId());
        assertThat(saved.getPatientName()).isEqualTo(command.patientName());
        assertThat(saved.getDoctorName()).isEqualTo(command.doctorName());
        assertThat(saved.getScheduledAt()).isEqualTo(command.scheduledAt());
        assertThat(saved.getEventType()).isEqualTo(AppointmentEventType.APPOINTMENT_CREATED);
    }

    @Test
    void handle_whenEventIsUpdatedAndStatusIsCanceled_buildsCanceledMessage_caseInsensitive() {
        AppointmentEventCommand command = command(AppointmentEventType.APPOINTMENT_UPDATED, "canceled");

        notificationService.handle(command);

        String expectedMessage = "Hello Jane Doe, your appointment with Dr. House scheduled for " + FORMATTED_DATE + " has been canceled.";
        verify(reminderSender).send(command.patientName(), expectedMessage);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getMessage()).isEqualTo(expectedMessage);
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void handle_whenEventIsUpdatedAndStatusIsUpperCaseCanceled_buildsCanceledMessage() {
        AppointmentEventCommand command = command(AppointmentEventType.APPOINTMENT_UPDATED, "CANCELED");

        notificationService.handle(command);

        String expectedMessage = "Hello Jane Doe, your appointment with Dr. House scheduled for " + FORMATTED_DATE + " has been canceled.";
        verify(reminderSender).send(command.patientName(), expectedMessage);
    }

    @Test
    void handle_whenEventIsUpdatedAndStatusIsNotCanceled_buildsUpdatedMessage() {
        AppointmentEventCommand command = command(AppointmentEventType.APPOINTMENT_UPDATED, "RESCHEDULED");

        notificationService.handle(command);

        String expectedMessage = "Hello Jane Doe, your appointment with Dr. House has been updated. New time: " + FORMATTED_DATE + ".";
        verify(reminderSender).send(command.patientName(), expectedMessage);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getMessage()).isEqualTo(expectedMessage);
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void handle_whenReminderSenderThrows_savesNotificationAsFailed_andDoesNotPropagate() {
        AppointmentEventCommand command = command(AppointmentEventType.APPOINTMENT_CREATED, null);
        doThrow(new RuntimeException("boom")).when(reminderSender).send(anyString(), anyString());

        notificationService.handle(command);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(captor.getValue().getAppointmentId()).isEqualTo(command.appointmentId());
    }

    @Test
    void list_delegatesToRepository() {
        List<Notification> notifications = List.of(Notification.builder().build());
        when(notificationRepository.findAll()).thenReturn(notifications);

        List<Notification> result = notificationService.list();

        assertThat(result).isEqualTo(notifications);
        verify(notificationRepository).findAll();
    }

    @Test
    void listByPatient_delegatesToRepository() {
        UUID patientId = UUID.randomUUID();
        List<Notification> notifications = List.of(Notification.builder().build());
        when(notificationRepository.findByPatientId(patientId)).thenReturn(notifications);

        List<Notification> result = notificationService.listByPatient(patientId);

        assertThat(result).isEqualTo(notifications);
        verify(notificationRepository).findByPatientId(patientId);
    }
}
