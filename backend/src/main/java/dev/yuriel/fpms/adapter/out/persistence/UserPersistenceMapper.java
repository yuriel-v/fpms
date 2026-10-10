package dev.yuriel.fpms.adapter.out.persistence;

import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;

/**
 * Translates between the JPA persistence model ({@link UserJpaEntity}) and the
 * domain {@link User} aggregate.
 *
 * <p>This is the only piece of the adapter worth unit-testing (project policy:
 * test the mapping, not the framework). Currency crosses the boundary as its
 * 3-letter ISO 4217 code on the persistence side and as a
 * {@link java.util.Currency} on the domain side.
 */
public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    /** Reconstructs a domain {@link User} from its persisted row. */
    public static User toDomain(UserJpaEntity entity) {
        return User.create(
                UserId.of(entity.getId()),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getDefaultCurrency());
    }

    /** Maps a domain {@link User} to its JPA persistence model. */
    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.email(),
                user.passwordHash(),
                user.defaultCurrency().getCurrencyCode());
    }
}
