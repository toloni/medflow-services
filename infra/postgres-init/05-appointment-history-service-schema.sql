-- appointment-history-service DDL (database: appointment_history_db)
\c appointment_history_db

CREATE TABLE IF NOT EXISTS appointment_history (
    id             UUID PRIMARY KEY,
    appointment_id UUID NOT NULL,
    patient_id     UUID NOT NULL,
    patient_name   VARCHAR(255) NOT NULL,
    doctor_id      UUID NOT NULL,
    doctor_name    VARCHAR(255),
    scheduled_at   TIMESTAMP WITH TIME ZONE,
    event_type     VARCHAR(255) NOT NULL,
    status         VARCHAR(255) NOT NULL,
    recorded_at    TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_appointment_history_appointment_id ON appointment_history (appointment_id);
CREATE INDEX IF NOT EXISTS idx_appointment_history_patient_id ON appointment_history (patient_id);
