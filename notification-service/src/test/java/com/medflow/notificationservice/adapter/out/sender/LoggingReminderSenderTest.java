package com.medflow.notificationservice.adapter.out.sender;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class LoggingReminderSenderTest {

    @Test
    void send_onlyLogs_doesNotThrow() {
        LoggingReminderSender sender = new LoggingReminderSender();

        assertThatCode(() -> sender.send("Jane Doe", "Hello Jane Doe"))
                .doesNotThrowAnyException();
    }
}
