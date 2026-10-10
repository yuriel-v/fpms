package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;

/**
 * Inbound port for retrieving the currently authenticated user — the inbound
 * (web) adapter resolves the {@link UserId} from a validated token and drives
 * this to fetch the full domain {@link User}.
 *
 * <p>Implemented by an application service; the application depends only on
 * this abstraction. Pure Java + domain types — no framework dependencies.
 */
public interface GetCurrentUserUseCase {

    /**
     * Fetches the user identified by the given id.
     *
     * @param userId the identity resolved from the validated token
     * @return the domain user
     * @throws UserNotFoundException if no user exists with the given id
     */
    User getCurrentUser(UserId userId);
}
