package com.medflow.appointmentservice.adapter.in.web;

import com.medflow.appointmentservice.domain.exception.AppointmentNotFoundException;
import com.medflow.appointmentservice.domain.exception.DoctorNotFoundException;
import com.medflow.appointmentservice.domain.exception.PatientNotFoundException;
import com.medflow.appointmentservice.domain.exception.UnauthorizedAppointmentAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/// Translates domain exceptions and validation failures into RFC 7807
/// [ProblemDetail] responses for all REST controllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AppointmentNotFoundException.class,
            DoctorNotFoundException.class,
            PatientNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedAppointmentAccessException.class)
    public ProblemDetail handleForbidden(UnauthorizedAppointmentAccessException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null
                                ? "invalid value"
                                : fieldError.getDefaultMessage(),
                        (first, second) -> first + "; " + second
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setProperty("errors", errors);
        return problem;
    }
}
