# Implementation Plan — FPMS MVP

- [x] 1. Repository skeleton + Docker Compose foundation
  - Create monorepo layout: `/backend`, `/frontend`, root `docker-compose.yml`, `.env.example`, `README.md`.
  - Stand up a PostgreSQL service with a named volume and env-driven credentials; add backend/frontend service placeholders.
  - Keep all DB settings in env vars so the DB can later be swapped for an external one.
  - _Demo:_ `docker compose up db` brings up a healthy Postgres container you can connect to.
  - _Requirements: 7.1, 7.4_

- [x] 2. Backend bootstrap with hexagonal layout + health check
  - Spring Boot 3.x on Java 21; package structure `domain`, `application` (`port.in`, `port.out`, `service`), `adapter.in.web`, `adapter.out.persistence`, `config`.
  - Wire Spring Data JPA + Flyway against the Compose Postgres; add an empty Flyway baseline.
  - Add an ArchUnit test asserting inward dependency direction; expose `/actuator/health`.
  - _Demo:_ backend starts, `/actuator/health` is UP against the DB, ArchUnit layering test passes.
  - _Requirements: 7.2, 8.1, 8.2_

- [ ] 3. Domain core — Money, Transaction, Category, User
  - Pure-Java domain: `Money` (amountMinor + currency, safe arithmetic + currency-mismatch guards), `TransactionType`, `Transaction`, `Category`, `User` (default currency), and id value objects.
  - Define inbound/outbound port interfaces for the use cases to come.
  - No Spring/JPA imports in `domain`; validate invariants in constructors.
  - _Demo:_ `./mvnw test` runs green domain unit tests (Money math, currency guards, invariants).
  - _Requirements: 4.2, 6.*, 8.1_

- [ ] 4. Auth — registration, login, JWT, user persistence
  - Register/login use cases, BCrypt hashing, JWT issuance/validation via Spring Security 6, JPA user adapter (owner-aware), Flyway `users` migration (incl. `default_currency`).
  - Keep token concerns in the security adapter behind an outbound port; protect all non-auth endpoints.
  - _Demo:_ `curl` register + login returns a JWT; token unlocks `/api/me`; no token → 401.
  - _Requirements: 1.*, 2.*_

- [ ] 5. Category management — end-to-end slice
  - Use cases + REST adapter + JPA adapter + Flyway `categories` migration; CRUD scoped to the authenticated user.
  - DTOs at the web edge mapped to domain; enforce user-scoping in the application layer.
  - _Demo:_ authenticated API calls create/list/edit/delete categories; user A cannot see user B's categories.
  - _Requirements: 3.*, 2.5, 8.3_

- [ ] 6. Transactions — end-to-end slice with monthly statement query
  - CRUD for one-time transactions (name, type, amountMinor, currency, from, to, occurredAt, optional category).
  - Statement query returning a month's transactions with income/expense/net totals grouped by currency; Flyway `transactions` migration with FK `user_id` and index `(user_id, occurred_at)`.
  - _Demo:_ API logs several entries and returns a monthly statement with correct per-currency totals.
  - _Requirements: 4.*, 5.*, 8.3_

- [ ] 7. Frontend bootstrap — Vite + React + TS + MUI + React Query + routing
  - Scaffold `react-ts`; add MUI, React Query (client provider), router, API client (env-based base URL), app shell/layout.
  - Add a currency formatter util (minor↔major units by currency exponent) with unit tests.
  - _Demo:_ `npm run dev` serves the app shell with navigation; `npm run build` succeeds; formatter tests pass.
  - _Requirements: 6.1, 6.2_

- [ ] 8. Frontend auth flow wired to the backend
  - Register/login pages, JWT storage, authenticated API layer (attach token, handle 401), protected routes, "logged in as {user}" + logout.
  - Keep auth state in a context/provider; redirect unauthenticated users to login.
  - _Demo:_ register + log in through the UI against the live backend; protected pages gated by auth.
  - _Requirements: 1.*, 2.*_

- [ ] 9. Statement UI — list, totals, month navigation
  - Monthly statement screen via React Query: table (name, from, to, date/time, formatted amount by currency, category, type), per-currency income/expense/net totals, month switcher, loading/empty/error states.
  - _Demo:_ logged-in user views a month's transactions and totals and navigates between months.
  - _Requirements: 5.*, 6.1_

- [ ] 10. Statement UI — create / edit / delete transactions and categories
  - Dialogs/forms to add/edit transactions (amount entered in major units → minor on submit; type, currency defaulting to user default, from/to, date/time, category picker) and to manage categories; React Query mutations with cache invalidation.
  - Client-side validation; invalidate the statement query on success.
  - _Demo:_ user adds/edits/removes entries and sees the statement + totals update live.
  - _Requirements: 3.*, 4.*, 6.2, 6.3_

- [ ] 11. Full containerization + end-to-end wiring
  - Dockerfiles for backend (multi-stage) and frontend (build + static serve with API proxy); finalize `docker-compose.yml` so `docker compose up` brings up db + backend + frontend with health checks and startup ordering; finalize README.
  - All cross-service config + JWT secret via env vars; document pointing at an external DB later; confirm migrations run on startup.
  - _Demo:_ one `docker compose up` launches the whole app; a new user registers, logs in, and manages a monthly statement in the browser.
  - _Requirements: 7.*_
