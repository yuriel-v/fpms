package dev.yuriel.fpms.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  private final UserId id = UserId.newId();

  @Test
  @DisplayName("Creating a user retains its id, email, password hash and default currency")
  void whenCreatingUserThenRetainsIdEmailHashAndDefaultCurrency() {
    User u = User.create(id, "me@example.com", "hashed-pw", "USD");
    assertEquals(id, u.id());
    assertEquals("me@example.com", u.email());
    assertEquals("hashed-pw", u.passwordHash());
    assertEquals(Currency.getInstance("USD"), u.defaultCurrency());
  }

  @Test
  @DisplayName("Creating a new user generates an id")
  void whenCreatingNewUserThenGeneratesAnId() {
    User u = User.newUser("me@example.com", "hashed-pw", "BRL");
    assertNotNull(u.id());
  }

  @Test
  @DisplayName("Creating a user normalizes the email to lowercase and trimmed")
  void whenCreatingUserThenNormalizesEmailToLowercaseAndTrimmed() {
    User u = User.create(id, "  Me@Example.COM  ", "hashed-pw", "USD");
    assertEquals("me@example.com", u.email());
  }

  @Test
  @DisplayName("Creating a user with null fields is rejected")
  void whenCreatingUserWithNullFieldsThenThrows() {
    assertThrows(NullPointerException.class, () -> User.create(null, "me@example.com", "h", "USD"));
    assertThrows(NullPointerException.class, () -> User.create(id, null, "h", "USD"));
    assertThrows(NullPointerException.class, () -> User.create(id, "me@example.com", null, "USD"));
    assertThrows(
        NullPointerException.class, () -> User.create(id, "me@example.com", "h", (String) null));
  }

  @Test
  @DisplayName("Creating a user with a blank email or hash is rejected")
  void whenCreatingUserWithBlankEmailOrHashThenThrows() {
    assertThrows(IllegalArgumentException.class, () -> User.create(id, "   ", "h", "USD"));
    assertThrows(
        IllegalArgumentException.class, () -> User.create(id, "me@example.com", "  ", "USD"));
  }

  @Test
  @DisplayName("Creating a user with a malformed email is rejected")
  void whenCreatingUserWithMalformedEmailThenThrows() {
    assertThrows(IllegalArgumentException.class, () -> User.create(id, "not-an-email", "h", "USD"));
  }

  @Test
  @DisplayName("Creating a user with an unknown default currency is rejected")
  void whenCreatingUserWithUnknownDefaultCurrencyThenThrows() {
    assertThrows(
        IllegalArgumentException.class, () -> User.create(id, "me@example.com", "h", "XYZ"));
  }

  @Test
  @DisplayName(
      "Changing the default currency returns a new instance and leaves the original unchanged")
  void whenChangingDefaultCurrencyThenReturnsNewInstanceLeavingOriginalUnchanged() {
    User u = User.create(id, "me@example.com", "h", "USD");
    User changed = u.changeDefaultCurrency(Currency.getInstance("EUR"));
    assertEquals(Currency.getInstance("EUR"), changed.defaultCurrency());
    assertEquals(Currency.getInstance("USD"), u.defaultCurrency());
  }
}
