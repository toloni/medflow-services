package com.medflow.notificationservice.adapter.out.sender;

import com.medflow.notificationservice.application.port.out.ReminderSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/// Logging-only stub implementation of [ReminderSenderPort] that writes the
/// reminder to the application log instead of dispatching it through a real
/// channel (e.g. email or SMS).
@Slf4j
@Component
class LoggingReminderSender implements ReminderSenderPort {

    @Override
    public void send(String patientName, String message) {
        log.info("[REMINDER -> {}] {}", patientName, message);
    }
}
