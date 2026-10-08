package dev.yuriel.fpms.domain;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionId;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdValueObjectsTest {

    @Test
    void wraps_a_provided_uuid() {
        UUID raw = UUID.randomUUID();
        assertEquals(raw, UserId.of(raw).value());
        assertEquals(raw, CategoryId.of(raw).value());
        assertEquals(raw, TransactionId.of(raw).value());
    }

    @Test
    void parses_a_uuid_string() {
        UUID raw = UUID.randomUUID();
        assertEquals(raw, UserId.of(raw.toString()).value());
    }

    @Test
    void generates_a_fresh_random_id() {
        assertNotNull(UserId.newId().value());
        assertNotEquals(UserId.newId(), UserId.newId());
    }

    @Test
    void rejects_null_uuid() {
        assertThrows(NullPointerException.class, () -> UserId.of((UUID) null));
        assertThrows(NullPointerException.class, () -> CategoryId.of((UUID) null));
        assertThrows(NullPointerException.class, () -> TransactionId.of((UUID) null));
    }

    @Test
    void equal_when_underlying_uuid_matches() {
        UUID raw = UUID.randomUUID();
        assertEquals(UserId.of(raw), UserId.of(raw));
        assertEquals(UserId.of(raw).hashCode(), UserId.of(raw).hashCode());
    }
}
