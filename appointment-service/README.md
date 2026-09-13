# appointment-service

Owns the appointment lifecycle: create, update, cancel, retrieve and list. Exposes a REST API secured with HTTP
Basic authentication and role-based authorization. On every create/update/cancel it publishes a domain event to the
Kafka topic `medflow.appointment-events`, consumed by [notification-service](../notification-service/README.md) and
[appointment-history-service](../appointment-history-service/README.md).

See the [root README](../README.md) for overall architecture, setup and running instructions.

## Domain

### Roles

| Role      | Can do                                                              |
|-----------|----------------------------------------------------------------------|
| `DOCTOR`  | Create/update/cancel appointments; view their own appointments       |
| `NURSE`   | Create/update/cancel any appointment; view every appointment         |
| `PATIENT` | View only their own appointments                                     |

### Appointment

| Field         | Type               | Description                                    |
|----------------|--------------------|--------------------------------------------------|
| `id`           | `UUID`             | Unique identifier                                |
| `patient`      | `Patient`          | Patient the appointment is for                   |
| `doctor`       | `Doctor`           | Doctor assigned to the appointment               |
| `createdBy`    | `User`             | User (doctor or nurse) who created the appointment |
| `scheduledAt`  | `Instant`          | Date/time the appointment is scheduled for       |
| `status`       | `AppointmentStatus`| `SCHEDULED` (default), `CONFIRMED`, `CANCELED`, `COMPLETED` |
| `notes`        | `String`           | Optional free-text notes (max 500 chars)         |
| `createdAt` / `updatedAt` | `Instant` | Managed automatically on persist/update           |

### Business Rules

- `patientId` and `doctorId` must reference existing `Patient`/`Doctor` records
- `scheduledAt` must be in the future on creation and, when provided, on update
- Only `DOCTOR` and `NURSE` roles may create, update or cancel appointments
- A `NURSE` can see and access every appointment
- A `DOCTOR` or `PATIENT` may only view appointments where they are the assigned doctor or the patient — otherwise
  the request is rejected with `403 Forbidden`
- Update is partial: any field left `null` in the request keeps its current value
- Cancelling an appointment sets its `status` to `CANCELED` and publishes an `APPOINTMENT_UPDATED` event (there is
  no distinct "canceled" event type — see the [root README](../README.md#when-is-a-reminder-sent))

## Events Published

Topic: `medflow.appointment-events`

| Event type              | Published on          |
|---------------------------|------------------------|
| `APPOINTMENT_CREATED`      | Appointment creation   |
| `APPOINTMENT_UPDATED`      | Appointment update or cancellation |

Payload (`AppointmentEventPayload`): `eventType`, `appointmentId`, `patientId`, `patientName`, `doctorId`,
`doctorName`, `scheduledAt`, `status`.

## API Endpoints

Base URL: `http://localhost:8080`, authenticated via HTTP Basic (username/password of a seeded user — see the
[root README](../README.md#2-services)).

| Method  | Route                              | Roles                   | Response Codes             |
|---------|-------------------------------------|--------------------------|------------------------------|
| POST    | `/api/appointments`                 | DOCTOR, NURSE            | `201`, `400`, `401`, `403`, `404` |
| PUT     | `/api/appointments/{id}`            | DOCTOR, NURSE            | `200`, `400`, `401`, `403`, `404` |
| PATCH   | `/api/appointments/{id}/cancel`     | DOCTOR, NURSE            | `200`, `401`, `403`, `404`   |
| GET     | `/api/appointments/{id}`            | DOCTOR, NURSE, PATIENT   | `200`, `401`, `403`, `404`   |
| GET     | `/api/appointments`                 | DOCTOR, NURSE, PATIENT   | `200`, `401`                 |

### Response Codes

| Code  | Description                                             |
|-------|-----------------------------------------------------------|
| `200` | OK — request succeeded                                    |
| `201` | Created — appointment created successfully                |
| `400` | Bad Request — validation failed (see `errors` in the response body) |
| `401` | Unauthorized — missing or invalid credentials              |
| `403` | Forbidden — requester lacks the role or ownership required |
| `404` | Not Found — appointment, doctor or patient not found        |

## Tests

Unit tests cover the domain model, `AppointmentService` use cases and the Kafka event publisher; `@WebMvcTest`
covers the controller and the global exception handler. Run with:

```bash
./mvnw test
```
