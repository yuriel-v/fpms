package dev.yuriel.fpms.domain.user;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    private final UserId id = UserId.newId();

    @Test
    void creates_a_user_with_id_email_hash_and_default_currency() {
        User u = User.create(id, "me@example.com", "hashed-pw", "USD");
        assertEquals(id, u.id());
        assertEquals("me@example.com", u.email());
        assertEquals("hashed-pw", u.passwordHash());
        assertEquals(Currency.getInstance("USD"), u.defaultCurrency());
    }

    @Test
    void new_user_generates_an_id() {
        User u = User.newUser("me@example.com", "hashed-pw", "BRL");
        assertNotNull(u.id());
    }

    @Test
    void normalizes_email_to_lowercase_and_trimmed() {
        User u = User.create(id, "  Me@Example.COM  ", "hashed-pw", "USD");
        assertEquals("me@example.com", u.email());
    }

    @Test
    void rejects_null_fields() {
        assertThrows(NullPointerException.class, () -> User.create(null, "me@example.com", "h", "USD"));
        assertThrows(NullPointerException.class, () -> User.create(id, null, "h", "USD"));
        assertThrows(NullPointerException.class, () -> User.create(id, "me@example.com", null, "USD"));
        assertThrows(NullPointerException.class, () -> User.create(id, "me@example.com", "h", (String) null));
    }

    @Test
    void rejects_blank_email_and_hash() {
        assertThrows(IllegalArgumentException.class, () -> User.create(id, "   ", "h", "USD"));
        assertThrows(IllegalArgumentException.class, () -> User.create(id, "me@example.com", "  ", "USD"));
    }

    @Test
    void rejects_malformed_email() {
        assertThrows(IllegalArgumentException.class, () -> User.create(id, "not-an-email", "h", "USD"));
    }

    @Test
    void rejects_unknown_default_currency() {
        assertThrows(IllegalArgumentException.class, () -> User.create(id, "me@example.com", "h", "XYZ"));
    }

    @Test
    void changes_default_currency_returning_a_new_instance() {
        User u = User.create(id, "me@example.com", "h", "USD");
        User changed = u.changeDefaultCurrency(Currency.getInstance("EUR"));
        assertEquals(Currency.getInstance("EUR"), changed.defaultCurrency());
        assertEquals(Currency.getInstance("USD"), u.defaultCurrency());
    }
}
