package com.medflow.appointmentservice.adapter.in.web;

import com.medflow.appointmentservice.domain.exception.AppointmentNotFoundException;
import com.medflow.appointmentservice.domain.exception.DoctorNotFoundException;
import com.medflow.appointmentservice.domain.exception.PatientNotFoundException;
import com.medflow.appointmentservice.domain.exception.UnauthorizedAppointmentAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleNotFound_appointmentNotFound_mapsTo404() {
        UUID id = UUID.randomUUID();
        ProblemDetail problem = handler.handleNotFound(new AppointmentNotFoundException(id));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getDetail()).isEqualTo("Appointment not found: " + id);
    }

    @Test
    void handleNotFound_doctorNotFound_mapsTo404() {
        UUID id = UUID.randomUUID();
        ProblemDetail problem = handler.handleNotFound(new DoctorNotFoundException(id));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getDetail()).isEqualTo("Doctor not found: " + id);
    }

    @Test
    void handleNotFound_patientNotFound_mapsTo404() {
        UUID id = UUID.randomUUID();
        ProblemDetail problem = handler.handleNotFound(new PatientNotFoundException(id));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getDetail()).isEqualTo("Patient not found: " + id);
    }

    @Test
    void handleForbidden_mapsTo403() {
        ProblemDetail problem = handler.handleForbidden(new UnauthorizedAppointmentAccessException());

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(problem.getDetail()).isEqualTo("You are not allowed to access this appointment");
    }

    @Test
    void handleValidation_singleFieldError_mapsMessageAndStatus() {
        FieldError fieldError = new FieldError("createAppointmentRequest", "patientId", "patientId is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ProblemDetail problem = handler.handleValidation(methodArgumentNotValidException);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getDetail()).isEqualTo("Validation failed");

        assert problem.getProperties() != null;
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problem.getProperties().get("errors");
        assertThat(errors).containsEntry("patientId", "patientId is required");
    }

    @Test
    void handleValidation_fieldErrorWithNullDefaultMessage_usesInvalidValueFallback() {
        FieldError fieldError = new FieldError("createAppointmentRequest", "doctorId", null, false, null, null, null);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ProblemDetail problem = handler.handleValidation(methodArgumentNotValidException);

        assert problem.getProperties() != null;
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problem.getProperties().get("errors");
        assertThat(errors).containsEntry("doctorId", "invalid value");
    }

    @Test
    void handleValidation_multipleErrorsOnSameField_areConcatenated() {
        FieldError firstError = new FieldError("updateAppointmentRequest", "notes", "notes must be at most 500 characters");
        FieldError secondError = new FieldError("updateAppointmentRequest", "notes", "notes must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(firstError, secondError));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ProblemDetail problem = handler.handleValidation(methodArgumentNotValidException);

        assert problem.getProperties() != null;
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problem.getProperties().get("errors");
        assertThat(errors).containsEntry("notes",
                "notes must be at most 500 characters; notes must not be blank");
    }
}
