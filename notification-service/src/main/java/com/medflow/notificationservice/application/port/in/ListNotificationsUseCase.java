package com.medflow.notificationservice.application.port.in;

import com.medflow.notificationservice.domain.model.Notification;

import java.util.List;

/// Use case for retrieving every recorded notification.
public interface ListNotificationsUseCase {
    List<Notification> list();
}
