package dev.yuriel.fpms.adapter.out.persistence;

import dev.yuriel.fpms.application.port.out.CategoryRepository;
import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Driven (persistence) adapter implementing the outbound {@link CategoryRepository} port. Delegates
 * storage to {@link CategoryJpaRepository} and converts between the JPA model and the domain via
 * {@link CategoryPersistenceMapper}.
 *
 * <p>Every operation forwards the owner so the underlying owner-scoped queries keep one user's
 * categories inaccessible to another.
 */
@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

  private final CategoryJpaRepository jpaRepository;

  @Override
  public Category save(Category category) {
    CategoryJpaEntity saved = jpaRepository.save(CategoryPersistenceMapper.toEntity(category));
    return CategoryPersistenceMapper.toDomain(saved);
  }

  @Override
  public Optional<Category> findByIdAndOwner(CategoryId id, UserId owner) {
    return jpaRepository
        .findByIdAndUserId(id.value(), owner.value())
        .map(CategoryPersistenceMapper::toDomain);
  }

  @Override
  public List<Category> findAllByOwner(UserId owner) {
    return jpaRepository.findAllByUserId(owner.value()).stream()
        .map(CategoryPersistenceMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByIdAndOwner(CategoryId id, UserId owner) {
    return jpaRepository.existsByIdAndUserId(id.value(), owner.value());
  }

  @Override
  @Transactional
  public void deleteByIdAndOwner(CategoryId id, UserId owner) {
    jpaRepository.deleteByIdAndUserId(id.value(), owner.value());
  }
}
