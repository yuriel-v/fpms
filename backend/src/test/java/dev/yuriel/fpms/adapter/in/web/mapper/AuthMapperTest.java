package dev.yuriel.fpms.adapter.in.web.mapper;

import dev.yuriel.fpms.adapter.in.web.dto.AuthResponse;
import dev.yuriel.fpms.adapter.in.web.dto.LoginRequest;
import dev.yuriel.fpms.adapter.in.web.dto.RegisterRequest;
import dev.yuriel.fpms.adapter.in.web.dto.UserResponse;
import dev.yuriel.fpms.application.port.in.AuthResult;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthMapperTest {

    private final AuthMapper mapper = new AuthMapperImpl();

    @Test
    @DisplayName("maps RegisterRequest to RegisterCommand with renamed fields")
    void whenMappingRegisterRequest_thenRenamesPasswordAndCurrency() {
        RegisterCommand command = mapper.toCommand(
                new RegisterRequest("me@example.com", "secret123", "USD"));

        assertEquals("me@example.com", command.email());
        assertEquals("secret123", command.rawPassword());
        assertEquals("USD", command.defaultCurrencyCode());
    }

    @Test
    @DisplayName("maps LoginRequest to LoginCommand with renamed password")
    void whenMappingLoginRequest_thenRenamesPassword() {
        LoginCommand command = mapper.toCommand(new LoginRequest("me@example.com", "secret123"));

        assertEquals("me@example.com", command.email());
        assertEquals("secret123", command.rawPassword());
    }

    @Test
    @DisplayName("maps AuthResult to AuthResponse, stringifying the user id")
    void whenMappingAuthResult_thenStringifiesUserId() {
        UUID raw = UUID.randomUUID();
        AuthResponse response = mapper.toResponse(
                new AuthResult("token-123", UserId.of(raw), "me@example.com"));

        assertEquals("token-123", response.token());
        assertEquals(raw.toString(), response.userId());
        assertEquals("me@example.com", response.email());
    }

    @Test
    @DisplayName("maps User to UserResponse, stringifying id and currency")
    void whenMappingUser_thenStringifiesIdAndCurrency() {
        UUID raw = UUID.randomUUID();
        User user = User.create(UserId.of(raw), "me@example.com", "hash", "EUR");

        UserResponse response = mapper.toResponse(user);

        assertEquals(raw.toString(), response.id());
        assertEquals("me@example.com", response.email());
        assertEquals("EUR", response.defaultCurrency());
    }
}
