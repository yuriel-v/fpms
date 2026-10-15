package dev.yuriel.fpms.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Currency;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

  private static final String EMAIL = "me@example.com";
  private static final String HASH = "hashed-pw";
  private static final String USD = "USD";

  private static final UserId ID = UserId.newId();

  @Test
  @DisplayName("Creating a user retains its id, email, password hash and default currency")
  void whenCreatingUserThenRetainsIdEmailHashAndDefaultCurrency() {
    User u = User.create(ID, EMAIL, HASH, USD);
    assertEquals(ID, u.id());
    assertEquals(EMAIL, u.email());
    assertEquals(HASH, u.passwordHash());
    assertEquals(Currency.getInstance(USD), u.defaultCurrency());
  }

  @Test
  @DisplayName("Creating a new user generates an id")
  void whenCreatingNewUserThenGeneratesAnId() {
    User u = User.newUser(EMAIL, HASH, "BRL");
    assertNotNull(u.id());
  }

  @Test
  @DisplayName("Creating a user normalizes the email to lowercase and trimmed")
  void whenCreatingUserThenNormalizesEmailToLowercaseAndTrimmed() {
    User u = User.create(ID, "  Me@Example.COM  ", HASH, USD);
    assertEquals(EMAIL, u.email());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("creationsWithANullRequiredField")
  @DisplayName("Creating a user with any null required field is rejected")
  void whenCreatingUserWithNullFieldThenThrows(String field, Executable creation) {
    assertThrows(NullPointerException.class, creation);
  }

  private static Stream<Arguments> creationsWithANullRequiredField() {
    return Stream.of(
        Arguments.of("null id", (Executable) () -> User.create(null, EMAIL, HASH, USD)),
        Arguments.of("null email", (Executable) () -> User.create(ID, null, HASH, USD)),
        Arguments.of("null hash", (Executable) () -> User.create(ID, EMAIL, null, USD)),
        Arguments.of(
            "null currency", (Executable) () -> User.create(ID, EMAIL, HASH, (String) null)));
  }

  @ParameterizedTest(name = "blank {0}")
  @ValueSource(strings = {"email", "hash"})
  @DisplayName("Creating a user with a blank email or hash is rejected")
  void whenCreatingUserWithBlankEmailOrHashThenThrows(String blankField) {
    Executable creation =
        "email".equals(blankField)
            ? () -> User.create(ID, "   ", HASH, USD)
            : () -> User.create(ID, EMAIL, "  ", USD);
    assertThrows(IllegalArgumentException.class, creation);
  }

  @Test
  @DisplayName("Creating a user with a malformed email is rejected")
  void whenCreatingUserWithMalformedEmailThenThrows() {
    assertThrows(IllegalArgumentException.class, () -> User.create(ID, "not-an-email", HASH, USD));
  }

  @Test
  @DisplayName("Creating a user with an unknown default currency is rejected")
  void whenCreatingUserWithUnknownDefaultCurrencyThenThrows() {
    assertThrows(IllegalArgumentException.class, () -> User.create(ID, EMAIL, HASH, "XYZ"));
  }

  @Test
  @DisplayName(
      "Changing the default currency returns a new instance and leaves the original unchanged")
  void whenChangingDefaultCurrencyThenReturnsNewInstanceLeavingOriginalUnchanged() {
    User u = User.create(ID, EMAIL, HASH, USD);
    User changed = u.changeDefaultCurrency(Currency.getInstance("EUR"));
    assertEquals(Currency.getInstance("EUR"), changed.defaultCurrency());
    assertEquals(Currency.getInstance(USD), u.defaultCurrency());
  }
}
