# appointment-history-service

Consumes appointment lifecycle events from Kafka and records each one as an append-only history entry. Exposes a
read-only GraphQL API, no authentication.

See the [root README](../README.md) for overall architecture, setup and running instructions.

## Domain

### AppointmentHistory

| Field           | Type                   | Description                                      |
|------------------|------------------------|-----------------------------------------------------|
| `id`             | `UUID`                 | Unique identifier                                   |
| `appointmentId`  | `UUID`                 | Appointment the entry refers to                     |
| `patientId` / `patientName` | `UUID` / `String` | Patient snapshot at the time of the event   |
| `doctorId` / `doctorName`   | `UUID` / `String` | Doctor snapshot at the time of the event    |
| `scheduledAt`    | `Instant`               | Appointment date/time at the time of the event      |
| `eventType`      | `AppointmentEventType`  | `APPOINTMENT_CREATED` or `APPOINTMENT_UPDATED`      |
| `status`         | `String`                | Appointment status at the time of the event         |
| `recordedAt`     | `Instant`               | Managed automatically on persist                    |

### Business Rules

- Entries are append-only — a history record is never updated or deleted, one entry is written per consumed event
- The service is a pure read/write facade over the event stream: it enforces no access control and does not
  reinterpret event data beyond storing a snapshot of it

## Events Consumed

Topic: `medflow.appointment-events`, consumer group `appointment-history-service`, reading from the earliest offset.

## GraphQL API

Endpoint: `POST http://localhost:8082/graphql`. Interactive console (GraphiQL): `http://localhost:8082/graphiql`.

| Query                                                      | Description                                              |
|--------------------------------------------------------------|-------------------------------------------------------------|
| `appointmentHistories`                                        | All history entries, most recent first                     |
| `appointmentHistoryByAppointment(appointmentId: ID!)`          | Timeline of one appointment, in chronological order         |
| `appointmentHistoriesByPatient(patientId: ID!)`                | History of every appointment of a patient, most recent first |

Schema: [`src/main/resources/graphql/schema.graphqls`](src/main/resources/graphql/schema.graphqls).

## Tests

Unit tests cover the domain model, `AppointmentHistoryService` use cases and the Kafka event listener; a
GraphQL slice test covers `AppointmentHistoryGraphQlController`. Run with:

```bash
./mvnw test
```
