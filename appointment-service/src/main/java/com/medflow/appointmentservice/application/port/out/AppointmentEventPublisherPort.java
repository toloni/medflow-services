package com.medflow.appointmentservice.application.port.out;

import com.medflow.appointmentservice.application.port.out.event.AppointmentEventType;
import com.medflow.appointmentservice.domain.model.Appointment;

/// Output port for publishing appointment lifecycle events to an external
/// messaging system.
public interface AppointmentEventPublisherPort {

    /// Publishes `eventType` for the given appointment.
    void publish(Appointment appointment, AppointmentEventType eventType);
}
