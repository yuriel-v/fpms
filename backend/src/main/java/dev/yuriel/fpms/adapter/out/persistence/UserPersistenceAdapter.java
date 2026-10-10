package dev.yuriel.fpms.adapter.out.persistence;

import dev.yuriel.fpms.application.port.out.UserRepository;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Driven (persistence) adapter implementing the outbound {@link UserRepository}
 * port. Delegates storage to {@link UserJpaRepository} and converts between the
 * JPA model and the domain via {@link UserPersistenceMapper}.
 *
 * <p>Email lookups normalize the input (trim + lowercase) to match the domain's
 * own normalization, so queries find rows regardless of input casing/whitespace.
 */
@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(normalizeEmail(email))
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(normalizeEmail(email));
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = jpaRepository.save(UserPersistenceMapper.toEntity(user));
        return UserPersistenceMapper.toDomain(saved);
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.strip().toLowerCase();
    }
}
