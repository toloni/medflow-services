-- notification-service DDL (database: notification_db)
\c notification_db

CREATE TABLE IF NOT EXISTS notifications (
    id            UUID PRIMARY KEY,
    appointment_id UUID NOT NULL,
    patient_id    UUID NOT NULL,
    patient_name  VARCHAR(255) NOT NULL,
    doctor_name   VARCHAR(255),
    scheduled_at  TIMESTAMP WITH TIME ZONE,
    event_type    VARCHAR(255) NOT NULL,
    channel       VARCHAR(255) NOT NULL,
    message       VARCHAR(1000) NOT NULL,
    status        VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
);
