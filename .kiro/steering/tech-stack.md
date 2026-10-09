# Tech Stack & Conventions — FPMS

## Stack
- **Backend:** Java 21, Spring Boot 3.x, Spring Security 6 (JWT), Spring Data JPA, Flyway.
- **Database:** PostgreSQL.
- **Frontend:** React + TypeScript via Vite; MUI; React Query; a router with protected routes.
- **Delivery:** Docker + Docker Compose. Backend built multi-stage; frontend built and served as static assets with an API proxy.

## Money
- Store amounts as **integer minor units** (`BIGINT`) plus an **ISO 4217 currency code**. Never store monetary values as floating point.
- A `Money` value object owns all monetary arithmetic and forbids mixing currencies.
- Totals are computed and reported **per currency**; never sum across currencies.
- The frontend converts minor↔major units using each currency's decimal exponent. New transactions default to the user's default currency.

## Persistence & migrations
- All schema changes go through **Flyway** versioned SQL migrations; migrations apply automatically on backend startup.
- JPA entities live only in the persistence adapter and are kept separate from domain types via mappers. The entity↔domain mappers are **unit-tested** (no database).
- Entity/schema agreement is guarded at startup by Hibernate `ddl-auto: validate` against the Flyway-migrated schema; we do not write tests that merely re-verify the framework's own CRUD behavior.
- Integration tests against **Testcontainers PostgreSQL** are **opt-in and off by default** (see Testing); reserve them for non-trivial, hand-written queries where real SQL correctness matters (e.g. the monthly statement aggregation), not for routine CRUD.

## Security
- Passwords hashed with BCrypt. JWT signing secret comes from an environment variable.
- Every data endpoint is scoped to the authenticated user (owner-aware). The application layer resolves a `UserId`, never Spring Security types directly.

## Configuration & deployment
- All cross-service configuration and secrets are provided via environment variables / `.env`.
- The database must be swappable for an external instance via configuration alone (change host/credentials, optionally drop the `db` service) — no code changes.
- `docker compose up` from a clean checkout must bring up db + backend + frontend, with health checks and startup ordering so the backend waits for a healthy database.

## Testing
- **Test-Driven Development (TDD) is the default workflow.** For each unit of
  behavior, follow red → green → refactor: write a failing test that pins the
  expected behavior first, confirm it fails for the right reason, then write the
  minimum production code to make it pass, then refactor with the test as a
  safety net. Commit tests and the code that satisfies them together.
- The **default build runs fast tests only**: domain unit tests, use-case tests with faked ports, entity↔domain mapper unit tests, and web-slice tests for controllers. `./mvnw test` must never start a database container.
- **Integration tests are opt-in.** Tag them `@Tag("integration")`; Surefire excludes that tag by default. Run them explicitly via a dedicated Maven profile (`./mvnw verify -Pintegration`). Keep them few and reserved for non-trivial hand-written queries (e.g. the monthly statement) rather than framework-handled CRUD.
- Do **not** test the framework: Spring Data JPA / Hibernate / the driver are trusted. Cover our own code (mappers, query logic, use cases) and rely on `ddl-auto: validate` for entity/schema drift.
- An **ArchUnit** test enforces the hexagonal dependency direction.
- Frontend: component tests for the currency formatter, auth forms, protected-route redirect, and statement rendering/totals.

## Prerequisites
- Node.js 20.19+ or 22.12+ for the Vite frontend.
- JDK 21 for the backend.
