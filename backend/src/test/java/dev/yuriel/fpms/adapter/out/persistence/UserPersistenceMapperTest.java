package dev.yuriel.fpms.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserPersistenceMapper}. Fast and pure: no Spring, no database — the project
 * policy is to test the mapping, not the framework.
 */
class UserPersistenceMapperTest {

  private final UUID uuid = UUID.randomUUID();

  @Test
  @DisplayName("Mapping an entity to domain and back preserves all fields")
  void whenMappingEntityToDomainAndBackThenPreservesAllFields() {
    UserJpaEntity entity = new UserJpaEntity(uuid, "me@example.com", "hashed-pw", "USD");

    User domain = UserPersistenceMapper.toDomain(entity);
    UserJpaEntity back = UserPersistenceMapper.toEntity(domain);

    assertEquals(entity.getId(), back.getId());
    assertEquals(entity.getEmail(), back.getEmail());
    assertEquals(entity.getPasswordHash(), back.getPasswordHash());
    assertEquals(entity.getDefaultCurrency(), back.getDefaultCurrency());
  }

  @Test
  @DisplayName("Mapping domain to entity stores the currency as a three-letter ISO code")
  void whenMappingDomainToEntityThenStoresCurrencyAsThreeLetterIsoCode() {
    User user =
        User.create(UserId.of(uuid), "me@example.com", "hashed-pw", Currency.getInstance("BRL"));

    UserJpaEntity entity = UserPersistenceMapper.toEntity(user);

    assertEquals("BRL", entity.getDefaultCurrency());
    assertEquals(uuid, entity.getId());
    assertEquals("me@example.com", entity.getEmail());
    assertEquals("hashed-pw", entity.getPasswordHash());
  }

  @Test
  @DisplayName("Mapping entity to domain reconstructs the currency from its ISO code")
  void whenMappingEntityToDomainThenReconstructsCurrencyFromIsoCode() {
    UserJpaEntity entity = new UserJpaEntity(uuid, "me@example.com", "hashed-pw", "EUR");

    User domain = UserPersistenceMapper.toDomain(entity);

    assertEquals(Currency.getInstance("EUR"), domain.defaultCurrency());
    assertEquals(UserId.of(uuid), domain.id());
    assertEquals("me@example.com", domain.email());
    assertEquals("hashed-pw", domain.passwordHash());
  }
}
