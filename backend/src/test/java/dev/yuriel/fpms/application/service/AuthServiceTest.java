package dev.yuriel.fpms.application.service;

import dev.yuriel.fpms.application.port.in.AuthResult;
import dev.yuriel.fpms.application.port.in.EmailAlreadyInUseException;
import dev.yuriel.fpms.application.port.in.InvalidCredentialsException;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.application.port.in.UserNotFoundException;
import dev.yuriel.fpms.application.port.out.PasswordHasher;
import dev.yuriel.fpms.application.port.out.TokenService;
import dev.yuriel.fpms.application.port.out.UserRepository;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fast unit tests for {@link AuthService} — no Spring context, no database.
 * Drives the three outbound ports with simple hand-written in-memory fakes.
 */
class AuthServiceTest {

    private InMemoryUserRepository users;
    private FakePasswordHasher hasher;
    private FakeTokenService tokens;
    private AuthService service;

    @BeforeEach
    void setUp() {
        users = new InMemoryUserRepository();
        hasher = new FakePasswordHasher();
        tokens = new FakeTokenService();
        service = new AuthService(users, hasher, tokens);
    }

    // ---- register ----------------------------------------------------------

    @Test
    void whenRegisteringNewUser_thenReturnsTokenAndIdentityAndPersistsHashedUser() {
        AuthResult result = service.register(new RegisterCommand("Me@Example.com", "s3cret", "USD"));

        // identity in the result reflects the (normalized) saved user
        assertNotNull(result.userId());
        assertEquals("me@example.com", result.email());
        assertEquals("token-for-" + result.userId().value(), result.token());

        // user is persisted and findable by its normalized email
        Optional<User> saved = users.findByEmail("me@example.com");
        assertTrue(saved.isPresent());
        assertEquals(result.userId(), saved.get().id());

        // password stored hashed, not raw
        assertEquals("hashed:s3cret", saved.get().passwordHash());
        assertFalse(saved.get().passwordHash().contains("s3cret") && saved.get().passwordHash().equals("s3cret"));
        assertEquals("me@example.com", saved.get().email());
    }

    @Test
    void whenRegisteringWithDuplicateEmail_thenThrowsAndDoesNotSave() {
        users.save(User.newUser("taken@example.com", "hashed:existing", "USD"));
        int before = users.count();

        assertThrows(EmailAlreadyInUseException.class,
                () -> service.register(new RegisterCommand("taken@example.com", "pw", "USD")));

        assertEquals(before, users.count());
    }

    @Test
    void whenRegisteringWithCaseInsensitiveDuplicateEmail_thenThrowsAndDoesNotSave() {
        users.save(User.newUser("taken@example.com", "hashed:existing", "USD"));
        int before = users.count();

        assertThrows(EmailAlreadyInUseException.class,
                () -> service.register(new RegisterCommand("TAKEN@Example.COM", "pw", "USD")));

        assertEquals(before, users.count());
    }

    // ---- login --------------------------------------------------------------

    @Test
    void whenLoggingInWithValidCredentials_thenReturnsToken() {
        User existing = users.save(User.newUser("me@example.com", hasher.hash("s3cret"), "USD"));

        AuthResult result = service.login(new LoginCommand("Me@Example.com", "s3cret"));

        assertEquals(existing.id(), result.userId());
        assertEquals("me@example.com", result.email());
        assertEquals("token-for-" + existing.id().value(), result.token());
    }

    @Test
    void whenLoggingInWithUnknownEmail_thenThrowsInvalidCredentials() {
        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new LoginCommand("nobody@example.com", "whatever")));
    }

    @Test
    void whenLoggingInWithWrongPassword_thenThrowsInvalidCredentials() {
        users.save(User.newUser("me@example.com", hasher.hash("right"), "USD"));

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new LoginCommand("me@example.com", "wrong")));
    }

    // ---- getCurrentUser -----------------------------------------------------

    @Test
    void whenGettingCurrentUserThatExists_thenReturnsUser() {
        User existing = users.save(User.newUser("me@example.com", "hashed:pw", "USD"));

        User found = service.getCurrentUser(existing.id());

        assertEquals(existing, found);
    }

    @Test
    void whenGettingCurrentUserThatIsAbsent_thenThrowsUserNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> service.getCurrentUser(UserId.newId()));
    }

    // ---- fakes --------------------------------------------------------------

    /** In-memory {@link UserRepository}; keys emails by the domain's normalized (lowercase/trim) form. */
    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UserId, User> byId = new HashMap<>();

        @Override
        public Optional<User> findByEmail(String email) {
            String key = normalize(email);
            return byId.values().stream().filter(u -> u.email().equals(key)).findFirst();
        }

        @Override
        public Optional<User> findById(UserId id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }

        @Override
        public User save(User user) {
            byId.put(user.id(), user);
            return user;
        }

        int count() {
            return byId.size();
        }

        private static String normalize(String email) {
            return email == null ? null : email.strip().toLowerCase();
        }
    }

    /** Deterministic, reversible fake hasher: {@code hash = "hashed:" + raw}. */
    private static final class FakePasswordHasher implements PasswordHasher {
        @Override
        public String hash(String rawPassword) {
            return "hashed:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            return hash(rawPassword).equals(passwordHash);
        }
    }

    /** Deterministic fake token service: {@code token = "token-for-" + userId.value()}. */
    private static final class FakeTokenService implements TokenService {
        @Override
        public String issueToken(UserId userId) {
            return "token-for-" + userId.value();
        }

        @Override
        public UserId validateAndExtractUserId(String token) {
            return UserId.of(token.substring("token-for-".length()));
        }
    }
}
