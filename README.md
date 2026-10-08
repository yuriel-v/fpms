# FPMS — Finance & Property Management System

A personal finances application. The MVP delivers a monthly **statement** of
one-time income and expense entries with full create/edit/remove/list
operations, behind real authentication.

The system is single-user at launch but modeled and architected so that
multi-user, recurring payments, and property/asset tracking can be added later
without a redesign.

## Tech stack

- **Backend:** Java 21, Spring Boot 3.x, Spring Security 6 (JWT), Spring Data
  JPA, Flyway — strict hexagonal architecture.
- **Database:** PostgreSQL.
- **Frontend:** React + TypeScript (Vite), MUI, React Query.
- **Delivery:** Docker + Docker Compose.

## Repository layout

```
fpms/
├── backend/            # Spring Boot API (Task 2+)
├── frontend/           # React + TypeScript app (Task 7+)
├── docker-compose.yml  # db + backend + frontend orchestration
├── .env.example        # all cross-service config & secrets (copy to .env)
└── README.md
```

## Prerequisites

- Docker + Docker Compose
- JDK 21 (for local backend development)
- Node.js 20.19+ or 22.12+ (for local frontend development)

## Getting started

1. Copy the environment template and adjust values as needed:

   ```bash
   cp .env.example .env
   ```

2. Bring up the database (available now):

   ```bash
   docker compose up db
   ```

   This starts a healthy PostgreSQL container with a named volume. Verify it is
   accepting connections:

   ```bash
   docker compose exec db pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"
   # or connect with psql:
   docker compose exec db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
   ```

3. The full stack (`docker compose up` for db + backend + frontend) comes
   online as the backend and frontend are implemented in later tasks. The
   `backend` and `frontend` services are currently placeholders behind the
   `backend` / `frontend` / `full` compose profiles.

## Configuration

All cross-service settings and secrets are provided via environment variables
(see `.env.example`). The database connection is fully env-driven, so you can
point the backend at an **external** PostgreSQL instance by changing the host
and credentials — and optionally removing the `db` service — with no code
changes.

| Variable | Purpose |
|----------|---------|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Database name & credentials |
| `POSTGRES_HOST` / `POSTGRES_PORT` | Database network location |
| `SPRING_DATASOURCE_URL` | Backend JDBC URL (override to target an external DB) |
| `BACKEND_PORT` | Backend published port |
| `JWT_SECRET` | JWT signing secret (override per environment) |
| `FRONTEND_PORT` | Frontend published port |
| `VITE_API_BASE_URL` | Frontend's base URL for the backend API |

## Implementation status

Delivered incrementally as vertical slices. See
`.kiro/specs/fpms/tasks.md` for the full plan. Task 1 (this skeleton + DB
foundation) is in place; later tasks add the backend, frontend, and full
containerized wiring.
