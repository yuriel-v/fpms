package dev.yuriel.fpms.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository over {@link UserJpaEntity}. A persistence-adapter
 * detail: the application depends only on the outbound port
 * {@code dev.yuriel.fpms.application.port.out.UserRepository}, never on this.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
