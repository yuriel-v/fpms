package dev.yuriel.fpms.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yuriel.fpms.application.port.in.AuthResult;
import dev.yuriel.fpms.application.port.in.EmailAlreadyInUseException;
import dev.yuriel.fpms.application.port.in.GetCurrentUserUseCase;
import dev.yuriel.fpms.application.port.in.InvalidCredentialsException;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.LoginUseCase;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.application.port.in.RegisterUserUseCase;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Fast web-slice tests for {@link AuthController}. {@code @WebMvcTest} loads
 * only the web layer; use cases are mocked and the real security filter chain
 * is disabled, so no database or Flyway starts.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    // Present so the controllers' advice/other beans in the slice resolve;
    // not exercised by these tests.
    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @Test
    @DisplayName("POST /api/auth/register with a valid body returns 201 and a token")
    void whenRegisteringWithValidBody_thenReturns201AndToken() throws Exception {
        UserId userId = new UserId(UUID.randomUUID());
        when(registerUserUseCase.register(any(RegisterCommand.class)))
                .thenReturn(new AuthResult("token-123", userId, "user@example.com"));

        String body = objectMapper.writeValueAsString(Map.of(
                "email", "user@example.com",
                "password", "secret123",
                "defaultCurrency", "USD"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-123"))
                .andExpect(jsonPath("$.userId").value(userId.value().toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register with an invalid body returns 400")
    void whenRegisteringWithInvalidBody_thenReturns400() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "not-an-email",
                "password", "",
                "defaultCurrency", "US"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register with an already-used email returns 409")
    void whenRegisteringWithEmailAlreadyInUse_thenReturns409() throws Exception {
        when(registerUserUseCase.register(any(RegisterCommand.class)))
                .thenThrow(new EmailAlreadyInUseException("email already in use"));

        String body = objectMapper.writeValueAsString(Map.of(
                "email", "user@example.com",
                "password", "secret123",
                "defaultCurrency", "USD"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("email already in use"));
    }

    @Test
    @DisplayName("POST /api/auth/login with valid credentials returns 200 and a token")
    void whenLoggingInWithValidCredentials_thenReturns200AndToken() throws Exception {
        UserId userId = new UserId(UUID.randomUUID());
        when(loginUseCase.login(any(LoginCommand.class)))
                .thenReturn(new AuthResult("login-token", userId, "user@example.com"));

        String body = objectMapper.writeValueAsString(Map.of(
                "email", "user@example.com",
                "password", "secret123"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login with invalid credentials returns 401")
    void whenLoggingInWithInvalidCredentials_thenReturns401() throws Exception {
        when(loginUseCase.login(any(LoginCommand.class)))
                .thenThrow(new InvalidCredentialsException("invalid credentials"));

        String body = objectMapper.writeValueAsString(Map.of(
                "email", "user@example.com",
                "password", "wrong"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("invalid credentials"));
    }
}
