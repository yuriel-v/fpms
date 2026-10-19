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
 * JPA persistence model for a category row in the {@code categories} table.
 *
 * <p>A driven-adapter detail, not a domain type: it stores the owner as a plain {@link UUID} and
 * the {@code TransactionType} as its {@code String} name, and lives isolated in the persistence
 * package. Column names must match {@code V3__categories.sql} exactly, since the app runs Hibernate
 * {@code ddl-auto: validate}.
 *
 * <p>The protected no-arg constructor is required by JPA; the all-args constructor is used by the
 * persistence mapper.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryJpaEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "type", nullable = false, length = 16)
  private String type;
}
