# FPMS — Finance & Property Management System

A personal finances app. The MVP delivers a monthly **statement** of one-time
income and expense entries — create, edit, remove, and list — behind real
authentication.

Single-user at launch, but modeled so that multi-user, recurring payments, and
property/asset tracking can be added later without a redesign.

## Tech stack

| Layer | Choice |
|-------|--------|
| Backend | Java 21, Spring Boot 3.x, Spring Security 6 (JWT), Spring Data JPA, Flyway — strict hexagonal architecture |
| Database | PostgreSQL |
| Frontend | React + TypeScript (Vite), MUI, React Query |
| Delivery | Docker + Docker Compose |

## Repository layout

```
fpms/
├── backend/            # Spring Boot API (Task 2+)
├── frontend/           # React + TypeScript app (Task 7+)
├── docker-compose.yml  # db + backend + frontend orchestration
├── .env.example        # cross-service config & secrets (copy to .env)
└── README.md
```

## Prerequisites

- Docker + Docker Compose
- JDK 21 — local backend development
- Node.js 20.19+ or 22.12+ — local frontend development

## Getting started

```bash
# 1. Create your local env file
cp .env.example .env

# 2. Bring up the database (available now)
docker compose up db

# 3. Verify it is accepting connections
docker compose exec db pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"
```

The full stack (`docker compose up` for db + backend + frontend) comes online
as later tasks land. The `backend` and `frontend` services are currently
placeholders behind the `backend` / `frontend` / `full` compose profiles.

## Configuration

All cross-service settings and secrets are env-driven (see `.env.example`). The
database connection is fully configurable, so you can point the backend at an
**external** PostgreSQL instance — change the host and credentials, optionally
drop the `db` service — with no code changes.

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

Built incrementally as vertical slices. The full plan lives in
`.kiro/specs/fpms/tasks.md`. Task 1 (repo skeleton + DB foundation) is in place;
later tasks add the backend, frontend, and full containerized wiring.

## License

Released under the [MIT License](LICENSE). Copyright (c) 2026 yuriel-v.
