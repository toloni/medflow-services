# notification-service

Consumes appointment lifecycle events from Kafka and sends a reminder to the patient (currently simulated via log —
the message is already recorded and queryable, so swapping `ReminderSenderPort` for a real email/SMS send is
trivial). Keeps a history of reminders in its own database. Exposes a read-only REST API, no authentication.

See the [root README](../README.md) for overall architecture, setup and running instructions.

## Domain

### Notification

| Field          | Type                  | Description                                    |
|-----------------|-----------------------|--------------------------------------------------|
| `id`            | `UUID`                | Unique identifier                                |
| `appointmentId` | `UUID`                | Appointment the reminder is about                |
| `patientId`     | `UUID`                | Patient the reminder was sent to                 |
| `patientName`   | `String`              | Patient name at the time of the event            |
| `doctorName`    | `String`              | Doctor name at the time of the event             |
| `scheduledAt`   | `Instant`              | Appointment date/time                            |
| `eventType`     | `AppointmentEventType`| `APPOINTMENT_CREATED` or `APPOINTMENT_UPDATED`   |
| `channel`       | `String`               | Delivery channel (currently always `EMAIL`)      |
| `message`       | `String`               | Rendered reminder text                           |
| `status`        | `NotificationStatus`   | `SENT` or `FAILED`                               |
| `createdAt`     | `Instant`              | Managed automatically on persist                 |

### Business Rules

- One `Notification` is recorded per consumed event, regardless of whether sending succeeds — a failed send is
  stored with `status = FAILED` instead of being retried or dropped
- The message text depends on the event type and, for updates, on the appointment's `status` — see the
  [root README](../README.md#when-is-a-reminder-sent) for the full mapping
- Date/time in the message is formatted using the `medflow.notification.date-format` and
  `medflow.notification.date-locale` properties (defaults: `dd/MM/yyyy HH:mm`, `pt-BR`)

## Events Consumed

Topic: `medflow.appointment-events`, consumer group `notification-service`, reading from the earliest offset.

## API Endpoints

Base URL: `http://localhost:8081`, no authentication required.

| Method | Route                                      | Description                                       | Response Codes |
|--------|----------------------------------------------|-----------------------------------------------------|------------------|
| GET    | `/api/notifications`                          | List all recorded notifications                     | `200`            |
| GET    | `/api/notifications/patient/{patientId}`      | List notifications for a specific patient           | `200`            |

## Tests

Unit tests cover the domain model, `NotificationService` use cases, the Kafka event listener and the logging sender
stub; `@WebMvcTest` covers the controller. Run with:

```bash
./mvnw test
```
