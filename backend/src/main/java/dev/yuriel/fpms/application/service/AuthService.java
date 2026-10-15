package dev.yuriel.fpms.application.service;

import dev.yuriel.fpms.application.port.in.AuthResult;
import dev.yuriel.fpms.application.port.in.EmailAlreadyInUseException;
import dev.yuriel.fpms.application.port.in.GetCurrentUserUseCase;
import dev.yuriel.fpms.application.port.in.InvalidCredentialsException;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.LoginUseCase;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.application.port.in.RegisterUserUseCase;
import dev.yuriel.fpms.application.port.in.UserNotFoundException;
import dev.yuriel.fpms.application.port.out.PasswordHasher;
import dev.yuriel.fpms.application.port.out.TokenService;
import dev.yuriel.fpms.application.port.out.UserRepository;
import dev.yuriel.fpms.domain.user.EmailNormalizer;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Objects;

/**
 * Application service implementing the authentication use cases — registration, login, and
 * resolving the current user.
 *
 * <p>Orchestrates the outbound ports ({@link UserRepository}, {@link PasswordHasher}, {@link
 * TokenService}) and the {@link User} domain aggregate. Deliberately framework-free: wiring into a
 * Spring bean lives in the config layer, keeping this layer pure and fast to unit-test.
 */
public class AuthService implements RegisterUserUseCase, LoginUseCase, GetCurrentUserUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final TokenService tokenService;

  public AuthService(
      UserRepository userRepository, PasswordHasher passwordHasher, TokenService tokenService) {
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
    this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher must not be null");
    this.tokenService = Objects.requireNonNull(tokenService, "tokenService must not be null");
  }

  @Override
  public AuthResult register(RegisterCommand command) {
    // Normalize to the same lowercase/trimmed form the domain uses, so a
    // case-only variant of an existing email is detected as a duplicate.
    String email = EmailNormalizer.normalize(command.email());
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyInUseException("email is already in use");
    }

    String passwordHash = passwordHasher.hash(command.rawPassword());
    User user = User.newUser(command.email(), passwordHash, command.defaultCurrencyCode());
    User saved = userRepository.save(user);

    String token = tokenService.issueToken(saved.id());
    return new AuthResult(token, saved.id(), saved.email());
  }

  @Override
  public AuthResult login(LoginCommand command) {
    String email = EmailNormalizer.normalize(command.email());
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("invalid email or password"));

    if (!passwordHasher.matches(command.rawPassword(), user.passwordHash())) {
      throw new InvalidCredentialsException("invalid email or password");
    }

    String token = tokenService.issueToken(user.id());
    return new AuthResult(token, user.id(), user.email());
  }

  @Override
  public User getCurrentUser(UserId userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException("no user exists for the given id"));
  }
}
