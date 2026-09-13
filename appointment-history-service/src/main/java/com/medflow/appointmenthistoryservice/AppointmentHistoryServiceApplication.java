package com.medflow.appointmenthistoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/// Entry point that bootstraps the appointment-history Spring Boot service.
@SpringBootApplication
public class AppointmentHistoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentHistoryServiceApplication.class, args);
    }

}
