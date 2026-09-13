package com.medflow.notificationservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void onCreate_setsCreatedAt() {
        Notification notification = Notification.builder().build();
        assertThat(notification.getCreatedAt()).isNull();

        Instant before = Instant.now();
        notification.onCreate();
        Instant after = Instant.now();

        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getCreatedAt()).isBetween(before, after);
    }
}
