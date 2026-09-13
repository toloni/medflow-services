package com.medflow.notificationservice.application.port.out;

/// Output port for dispatching a reminder message to a patient through
/// whatever channel the adapter implements.
public interface ReminderSenderPort {
    void send(String patientName, String message);
}
