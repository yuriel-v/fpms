package dev.yuriel.fpms.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA persistence model for a user row in the {@code users} table.
 *
 * <p>This is a driven-adapter detail, not a domain type: it carries the ISO 4217
 * currency as a plain {@code String} code rather than {@link java.util.Currency},
 * and lives isolated in the persistence package. Column names must match
 * {@code V2__users.sql} exactly, since the app runs Hibernate
 * {@code ddl-auto: validate}.
 */
@Entity
@Table(name = "users")
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

    /** Required no-arg constructor for JPA. */
    protected UserJpaEntity() {
    }

    public UserJpaEntity(UUID id, String email, String passwordHash, String defaultCurrency) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.defaultCurrency = defaultCurrency;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
