# Requirements — Finance & Property Management System (FPMS)

## Introduction

FPMS is a personal finances and (eventually) property management application. The
MVP delivers a monthly financial **statement** of one-time income and expense
entries with full create/edit/remove/list operations, behind real authentication.

The system is **single-user** at launch but is modeled and architected so that
multi-user, recurring payments, and property/asset tracking can be added later
without a redesign.

### Technical constraints (fixed by the user)
- Backend: Java 21 + Spring Boot, **strict hexagonal architecture**.
- Persistence: PostgreSQL via Spring Data JPA + Flyway migrations.
- Frontend: React + TypeScript (Vite), MUI, React Query.
- Delivery: fully containerized via Docker Compose (DB swappable for an external
  DB via configuration later).
- Money stored internally as **integer minor units** (e.g. cents) with a
  **currency per transaction**; a **default currency on the user account**;
  decimal formatting applied on the frontend.

## MVP Scope

In scope: authentication, categories, one-time transactions, and the monthly
statement view with per-currency totals.

Explicitly deferred (seams preserved, see Design): recurring templates and pay
windows, overdue flagging/roll-forward, near-due attention cues, properties/assets
and transaction↔property/income-source links, and tax reporting.

## Requirements

### Requirement 1 — User registration
**User Story:** As a new user, I want to register an account with my email,
password, and a default currency, so that I can securely access my own finances.

#### Acceptance Criteria
1. WHEN a user submits a unique email, a valid password, and a default currency THEN the system SHALL create an account with a securely hashed password.
2. IF the email is already registered THEN the system SHALL reject the registration with a clear error.
3. IF the password does not meet the minimum policy THEN the system SHALL reject the registration with a validation error.
4. WHEN an account is created THEN the system SHALL persist the chosen default currency as an ISO 4217 code on the user.

### Requirement 2 — Authentication & protected access
**User Story:** As a registered user, I want to log in and receive a token, so
that only I can access my data.

#### Acceptance Criteria
1. WHEN a user logs in with valid credentials THEN the system SHALL return a signed JWT.
2. IF credentials are invalid THEN the system SHALL respond with 401 and SHALL NOT reveal which field was wrong.
3. WHEN a request to a protected endpoint includes a valid JWT THEN the system SHALL process it as the authenticated user.
4. IF a protected endpoint is called without a valid JWT THEN the system SHALL respond with 401.
5. WHEN any data operation runs THEN the system SHALL scope it to the authenticated user (owner-aware).

### Requirement 3 — Category management
**User Story:** As a user, I want to manage categories, so that I can classify my
income and expenses.

#### Acceptance Criteria
1. WHEN a user creates a category with a name and type THEN the system SHALL persist it scoped to that user.
2. WHEN a user lists categories THEN the system SHALL return only that user's categories.
3. WHEN a user edits or deletes a category they own THEN the system SHALL apply the change.
4. IF a user attempts to access or modify a category they do not own THEN the system SHALL deny the operation.

### Requirement 4 — One-time transactions
**User Story:** As a user, I want to record one-time income and expense entries,
so that I can track money moving in and out.

#### Acceptance Criteria
1. WHEN a user creates a transaction THEN the system SHALL store name, type (INCOME or EXPENSE), amount in minor units, currency, source ("where from"), destination ("where to"), occurrence date/time, and an optional category.
2. WHEN an amount is stored THEN the system SHALL store it as an integer in minor units together with its currency; the system SHALL NOT store amounts as floating-point values.
3. WHEN a user edits or deletes a transaction they own THEN the system SHALL apply the change.
4. IF a transaction references a category THEN that category SHALL belong to the same user.
5. IF a user attempts to access or modify a transaction they do not own THEN the system SHALL deny the operation.

### Requirement 5 — Monthly statement
**User Story:** As a user, I want to view a given month's transactions with totals,
so that I can understand my financial position for that period.

#### Acceptance Criteria
1. WHEN a user requests the statement for a month THEN the system SHALL return every transaction whose occurrence date falls within that month for that user.
2. WHEN totals are computed THEN the system SHALL report income, expense, and net totals grouped by currency and SHALL NOT sum amounts across different currencies.
3. WHEN a user navigates to another month THEN the system SHALL return that month's data.
4. WHEN a month has no transactions THEN the system SHALL return an empty statement with zeroed totals.

### Requirement 6 — Money representation & display
**User Story:** As a user, I want amounts shown correctly for each currency, so
that values are never misrepresented.

#### Acceptance Criteria
1. WHEN the frontend displays an amount THEN it SHALL convert minor units to the display value using the currency's decimal exponent.
2. WHEN a user enters an amount in major units THEN the frontend SHALL convert it to minor units before submission.
3. WHEN a new transaction is created in the UI THEN the currency SHALL default to the user's default currency and remain changeable per transaction.

### Requirement 7 — Containerized delivery
**User Story:** As the operator (me), I want to run the whole system with one
command, so that setup is trivial.

#### Acceptance Criteria
1. WHEN `docker compose up` is run from a clean checkout THEN the system SHALL start PostgreSQL, the backend, and the frontend, wired together.
2. WHEN the backend starts THEN it SHALL apply pending Flyway migrations automatically.
3. WHEN services start THEN the system SHALL respect health checks and startup ordering so the backend waits for a healthy database.
4. WHEN configuration is needed THEN all cross-service settings and secrets SHALL be provided via environment variables so the database can later be pointed at an external instance via configuration only.

### Requirement 8 — Architecture & quality
**User Story:** As the maintainer, I want a clean, testable codebase, so that the
deferred features slot in without a rewrite.

#### Acceptance Criteria
1. The domain core SHALL NOT depend on Spring, JPA, or any framework.
2. Dependencies SHALL point inward (adapters → application → domain); a layering test (e.g. ArchUnit) SHALL enforce this.
3. Each MVP backend capability SHALL be delivered as a vertical slice (domain, application port, inbound and outbound adapters) with tests.
4. The schema and domain SHALL leave seams (owner-aware entities, extensible frequency concept) for recurring payments and properties without requiring their presence in the MVP.
