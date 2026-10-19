-- V3__categories.sql
-- Categories table (Task 5: category management).
--
-- Introduces the owner-scoped `categories` aggregate: a user's classification of
-- income or expense. Mirrors the CategoryJpaEntity persistence adapter exactly so
-- Hibernate `ddl-auto: validate` passes at startup — column names and types must
-- stay in lockstep.
--   id       UUID, primary key
--   user_id  owning user (FK -> users.id); every row is owner-scoped
--   name     category name (trimmed, non-blank by the domain)
--   type     TransactionType enum stored as text (INCOME | EXPENSE)
--
-- Indexed by user_id since every query is scoped to the owner.
create table categories (
    id      uuid         primary key,
    user_id uuid         not null,
    name    varchar(255) not null,
    type    varchar(16)  not null,
    constraint fk_categories_user foreign key (user_id) references users (id)
);

create index idx_categories_user_id on categories (user_id);
