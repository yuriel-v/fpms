package dev.yuriel.fpms.application.port.out;

import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Optional;

/**
 * Outbound port for user persistence — the application's view of where
 * {@link User} aggregates are stored and retrieved.
 *
 * <p>The driven (persistence) adapter implements this; the application depends
 * only on this abstraction. Pure Java + domain types — no framework
 * dependencies.
 */
public interface UserRepository {

    /**
     * Finds a user by login email.
     *
     * @param email the login email to look up
     * @return the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by identity.
     *
     * @param id the user identity to look up
     * @return the matching user, or empty if none exists
     */
    Optional<User> findById(UserId id);

    /**
     * Reports whether a user with the given email already exists.
     *
     * @param email the login email to check
     * @return {@code true} if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Persists a new or updated user.
     *
     * @param user the user to store
     * @return the stored user
     */
    User save(User user);
}
