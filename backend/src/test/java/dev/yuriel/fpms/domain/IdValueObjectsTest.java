package dev.yuriel.fpms.domain;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionId;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdValueObjectsTest {

    @Test
    @DisplayName("An id wraps a provided UUID")
    void whenCreatingIdFromUuid_thenWrapsThatUuid() {
        UUID raw = UUID.randomUUID();
        assertEquals(raw, UserId.of(raw).value());
        assertEquals(raw, CategoryId.of(raw).value());
        assertEquals(raw, TransactionId.of(raw).value());
    }

    @Test
    @DisplayName("An id parses a UUID string")
    void whenCreatingIdFromString_thenParsesTheUuid() {
        UUID raw = UUID.randomUUID();
        assertEquals(raw, UserId.of(raw.toString()).value());
    }

    @Test
    @DisplayName("Generating a new id yields a fresh random value each time")
    void whenGeneratingNewId_thenYieldsFreshRandomValue() {
        assertNotNull(UserId.newId().value());
        assertNotEquals(UserId.newId(), UserId.newId());
    }

    @Test
    @DisplayName("Creating an id from a null UUID is rejected")
    void whenCreatingIdFromNullUuid_thenThrows() {
        assertThrows(NullPointerException.class, () -> UserId.of((UUID) null));
        assertThrows(NullPointerException.class, () -> CategoryId.of((UUID) null));
        assertThrows(NullPointerException.class, () -> TransactionId.of((UUID) null));
    }

    @Test
    @DisplayName("Ids are equal when their underlying UUID matches")
    void whenUnderlyingUuidMatches_thenIdsAreEqual() {
        UUID raw = UUID.randomUUID();
        assertEquals(UserId.of(raw), UserId.of(raw));
        assertEquals(UserId.of(raw).hashCode(), UserId.of(raw).hashCode());
    }
}
