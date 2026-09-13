package com.medflow.appointmenthistoryservice.adapter.in.graphql;

import com.medflow.appointmenthistoryservice.adapter.in.graphql.dto.AppointmentHistoryEntry;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryByAppointmentUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryByPatientUseCase;
import com.medflow.appointmenthistoryservice.application.port.in.ListAppointmentHistoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

/// Exposes GraphQL queries for reading appointment history: all entries, entries for a
/// given appointment, and entries for a given patient.
@Controller
@RequiredArgsConstructor
class AppointmentHistoryGraphQlController {

    private final ListAppointmentHistoryUseCase listAppointmentHistoryUseCase;
    private final ListAppointmentHistoryByAppointmentUseCase listAppointmentHistoryByAppointmentUseCase;
    private final ListAppointmentHistoryByPatientUseCase listAppointmentHistoryByPatientUseCase;

    @QueryMapping
    public List<AppointmentHistoryEntry> appointmentHistories() {
        return listAppointmentHistoryUseCase.list().stream()
                .map(AppointmentHistoryEntry::from)
                .toList();
    }

    @QueryMapping
    public List<AppointmentHistoryEntry> appointmentHistoryByAppointment(@Argument UUID appointmentId) {
        return listAppointmentHistoryByAppointmentUseCase.listByAppointment(appointmentId).stream()
                .map(AppointmentHistoryEntry::from)
                .toList();
    }

    @QueryMapping
    public List<AppointmentHistoryEntry> appointmentHistoriesByPatient(@Argument UUID patientId) {
        return listAppointmentHistoryByPatientUseCase.listByPatient(patientId).stream()
                .map(AppointmentHistoryEntry::from)
                .toList();
    }
}
