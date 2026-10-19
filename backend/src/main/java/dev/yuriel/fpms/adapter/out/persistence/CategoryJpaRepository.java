package dev.yuriel.fpms.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository over {@link CategoryJpaEntity}. A persistence-adapter detail: the
 * application depends only on the outbound port {@code
 * dev.yuriel.fpms.application.port.out.CategoryRepository}, never on this.
 *
 * <p>Every finder is owner-scoped (by {@code userId}) so one user can never reach another's rows.
 */
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {

  Optional<CategoryJpaEntity> findByIdAndUserId(UUID id, UUID userId);

  List<CategoryJpaEntity> findAllByUserId(UUID userId);

  boolean existsByIdAndUserId(UUID id, UUID userId);

  void deleteByIdAndUserId(UUID id, UUID userId);
}
