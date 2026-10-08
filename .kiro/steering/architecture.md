# Architecture — Strict Hexagonal (Ports & Adapters)

The backend follows hexagonal architecture **strictly**. Keep the code clean,
organized, easy to maintain, and easy to test.

## Layers & dependency rule
Dependencies always point **inward**: `adapter` → `application` → `domain`.

- **domain** — pure Java. Entities, value objects (`Money`, id wrappers),
  enums, and domain rules. **No** Spring, JPA, Jackson, or any framework imports.
- **application** — use-case services plus **ports**:
  - `port.in` — inbound ports (use-case interfaces the adapters call).
  - `port.out` — outbound ports (interfaces the application needs: repositories,
    token service, clock, etc.).
  - `service` — implementations that orchestrate domain objects through ports.
- **adapter**
  - `adapter.in.web` — REST controllers, request/response DTOs, mappers. Translates
    HTTP ↔ inbound ports. Resolves the authenticated `UserId` here.
  - `adapter.out.persistence` — JPA entities, Spring Data repositories, and
    implementations of outbound ports. Maps between JPA entities and domain types.
- **config** — Spring wiring and security configuration only.

## Package layout
```
com.fpms
├── domain/{user,category,transaction,money}
├── application/{port.in,port.out,service}
├── adapter/in/web
├── adapter/out/persistence
└── config
```

## Rules
1. The domain never imports a framework. If a domain operation needs the outside
   world (persistence, time, tokens), express it as an **outbound port** and
   implement it in an adapter.
2. Controllers depend on **inbound ports**, not on service classes directly.
3. JPA entities are **not** domain entities. Map between them explicitly.
4. Enforce the inward dependency direction with an **ArchUnit** test.
5. Deliver each capability as a **vertical slice**: domain type(s) → inbound port
   → service → inbound adapter → outbound adapter → migration → tests.
6. Preserve seams for deferred features so they slot in without a rewrite:
   - All entities are **owner-aware** (`UserId`) for future multi-user.
   - The recurring-payment concept will introduce an **extensible `Frequency`**
     (MONTHLY, YEARLY, …) and per-period pay-window state; do not hardcode
     assumptions that would block adding frequencies.
   - Transactions will later gain optional links to a **property** and/or an
     **income source**; keep the transaction model open to optional associations.
