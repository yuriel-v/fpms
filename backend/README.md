# FPMS Backend

Placeholder for the Spring Boot backend (Java 21, strict hexagonal architecture).

This directory is scaffolded in **Task 2 — Backend bootstrap with hexagonal
layout + health check** and fleshed out across subsequent tasks:

- Spring Boot 3.x project with the hexagonal package layout
  (`domain`, `application.{port.in,port.out,service}`, `adapter.{in.web,out.persistence}`, `config`).
- Spring Data JPA + Flyway wired against the Compose PostgreSQL.
- ArchUnit test enforcing the inward dependency direction.
- `/actuator/health` endpoint.

Until then, the `backend` service in `docker-compose.yml` is a placeholder.
