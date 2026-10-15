package dev.yuriel.fpms.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CategoryPersistenceMapper}. Fast and pure: no Spring, no database — the
 * project policy is to test the mapping, not the framework.
 */
class CategoryPersistenceMapperTest {

  private final UUID id = UUID.randomUUID();
  private final UUID userId = UUID.randomUUID();

  @Test
  @DisplayName("Mapping an entity to domain and back preserves all fields")
  void whenMappingEntityToDomainAndBackThenPreservesAllFields() {
    CategoryJpaEntity entity = new CategoryJpaEntity(id, userId, "Salary", "INCOME");

    Category domain = CategoryPersistenceMapper.toDomain(entity);
    CategoryJpaEntity back = CategoryPersistenceMapper.toEntity(domain);

    assertEquals(entity.getId(), back.getId());
    assertEquals(entity.getUserId(), back.getUserId());
    assertEquals(entity.getName(), back.getName());
    assertEquals(entity.getType(), back.getType());
  }

  @Test
  @DisplayName("Mapping domain to entity stores the type as its enum name and owner as a UUID")
  void whenMappingDomainToEntityThenStoresTypeNameAndOwnerUuid() {
    Category category =
        Category.create(CategoryId.of(id), UserId.of(userId), "Rent", TransactionType.EXPENSE);

    CategoryJpaEntity entity = CategoryPersistenceMapper.toEntity(category);

    assertEquals(id, entity.getId());
    assertEquals(userId, entity.getUserId());
    assertEquals("Rent", entity.getName());
    assertEquals("EXPENSE", entity.getType());
  }

  @Test
  @DisplayName("Mapping entity to domain reconstructs the id, owner and type")
  void whenMappingEntityToDomainThenReconstructsIdOwnerAndType() {
    CategoryJpaEntity entity = new CategoryJpaEntity(id, userId, "Groceries", "EXPENSE");

    Category domain = CategoryPersistenceMapper.toDomain(entity);

    assertEquals(CategoryId.of(id), domain.id());
    assertEquals(UserId.of(userId), domain.owner());
    assertEquals("Groceries", domain.name());
    assertEquals(TransactionType.EXPENSE, domain.type());
  }
}
