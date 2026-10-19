-- V2__users.sql
-- Users table (Task 4: auth).
--
-- Introduces the `users` aggregate: identity, login email, hashed password, and
-- the ISO 4217 default currency new transactions start in. Mirrors the
-- UserJpaEntity persistence adapter exactly so Hibernate `ddl-auto: validate`
-- passes at startup — column names and types must stay in lockstep.
--   id               UUID, primary key
--   email            unique login address (normalized lowercase by the domain)
--   password_hash    BCrypt hash (60 chars; VARCHAR(255) leaves headroom)
--   default_currency ISO 4217 code (always 3 chars)
create table users (
    id               uuid         primary key,
    email            varchar(255) not null,
    password_hash    varchar(255) not null,
    default_currency varchar(3)   not null,
    constraint uq_users_email unique (email)
);
