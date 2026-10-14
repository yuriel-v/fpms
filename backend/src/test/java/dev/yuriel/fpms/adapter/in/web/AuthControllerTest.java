package dev.yuriel.fpms.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.yuriel.fpms.adapter.in.web.dto.AuthResponse;
import dev.yuriel.fpms.adapter.in.web.dto.LoginRequest;
import dev.yuriel.fpms.adapter.in.web.dto.RegisterRequest;
import dev.yuriel.fpms.adapter.in.web.mapper.AuthMapperImpl;
import dev.yuriel.fpms.application.port.in.AuthResult;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.LoginUseCase;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.application.port.in.RegisterUserUseCase;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test for {@link AuthController}: the controller is constructed directly
 * with mocked use-case ports and a real {@link AuthMapperImpl}. It verifies the
 * controller's own behavior — request→command mapping, result→response mapping,
 * and HTTP status — without loading any Spring context. Framework concerns
 * (bean validation, JSON (de)serialization) are intentionally out of scope.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private LoginUseCase loginUseCase;

    private AuthController controller() {
        return new AuthController(registerUserUseCase, loginUseCase, new AuthMapperImpl());
    }

    @Test
    @DisplayName("register maps the request to a command and returns 201 with the token")
    void whenRegistering_thenMapsCommandAndReturns201() {
        UserId userId = UserId.of(UUID.randomUUID());
        when(registerUserUseCase.register(any(RegisterCommand.class)))
                .thenReturn(new AuthResult("token-123", userId, "user@example.com"));

        ResponseEntity<AuthResponse> response = controller()
                .register(new RegisterRequest("user@example.com", "secret123", "USD"));

        ArgumentCaptor<RegisterCommand> captor = ArgumentCaptor.forClass(RegisterCommand.class);
        org.mockito.Mockito.verify(registerUserUseCase).register(captor.capture());
        RegisterCommand command = captor.getValue();
        assertEquals("user@example.com", command.email());
        assertEquals("secret123", command.rawPassword());
        assertEquals("USD", command.defaultCurrencyCode());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("token-123", response.getBody().token());
        assertEquals(userId.value().toString(), response.getBody().userId());
        assertEquals("user@example.com", response.getBody().email());
    }

    @Test
    @DisplayName("login maps the request to a command and returns 200 with the token")
    void whenLoggingIn_thenMapsCommandAndReturns200() {
        UserId userId = UserId.of(UUID.randomUUID());
        when(loginUseCase.login(any(LoginCommand.class)))
                .thenReturn(new AuthResult("login-token", userId, "user@example.com"));

        ResponseEntity<AuthResponse> response = controller()
                .login(new LoginRequest("user@example.com", "secret123"));

        ArgumentCaptor<LoginCommand> captor = ArgumentCaptor.forClass(LoginCommand.class);
        org.mockito.Mockito.verify(loginUseCase).login(captor.capture());
        assertEquals("user@example.com", captor.getValue().email());
        assertEquals("secret123", captor.getValue().rawPassword());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("login-token", response.getBody().token());
        assertEquals("user@example.com", response.getBody().email());
    }
}
