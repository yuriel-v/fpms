package dev.yuriel.fpms.adapter.out.persistence;

import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;

/**
 * Translates between the JPA persistence model ({@link CategoryJpaEntity}) and the domain {@link
 * Category} aggregate.
 *
 * <p>This is the only piece of the adapter worth unit-testing (project policy: test the mapping,
 * not the framework). The owner crosses the boundary as a raw {@link java.util.UUID} on the
 * persistence side and a {@link UserId} on the domain side; the {@link TransactionType} is stored
 * as its enum name.
 */
public final class CategoryPersistenceMapper {

  private CategoryPersistenceMapper() {}

  /** Reconstructs a domain {@link Category} from its persisted row. */
  public static Category toDomain(CategoryJpaEntity entity) {
    return Category.create(
        CategoryId.of(entity.getId()),
        UserId.of(entity.getUserId()),
        entity.getName(),
        TransactionType.valueOf(entity.getType()));
  }

  /** Maps a domain {@link Category} to its JPA persistence model. */
  public static CategoryJpaEntity toEntity(Category category) {
    return new CategoryJpaEntity(
        category.id().value(), category.owner().value(), category.name(), category.type().name());
  }
}
