package dev.yuriel.fpms.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence model for a user row in the {@code users} table.
 *
 * <p>This is a driven-adapter detail, not a domain type: it carries the ISO 4217 currency as a
 * plain {@code String} code rather than {@link java.util.Currency}, and lives isolated in the
 * persistence package. Column names must match {@code V2__users.sql} exactly, since the app runs
 * Hibernate {@code ddl-auto: validate}.
 *
 * <p>The protected no-arg constructor is required by JPA; the all-args constructor is used by the
 * persistence mapper.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserJpaEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "default_currency", nullable = false, length = 3)
  private String defaultCurrency;
}
