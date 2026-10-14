package dev.yuriel.fpms.adapter.in.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.yuriel.fpms.application.port.in.GetCurrentUserUseCase;
import dev.yuriel.fpms.application.port.in.LoginUseCase;
import dev.yuriel.fpms.application.port.in.RegisterUserUseCase;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Fast web-slice test for {@link MeController}. The real filter chain is
 * disabled; the {@link UserId} principal is supplied directly via an
 * {@code authentication(...)} request post-processor so
 * {@code @AuthenticationPrincipal} resolves it.
 */
@WebMvcTest(MeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    // Present so controller-related beans in the slice resolve; unused here.
    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/me with an authenticated user id returns 200 and the user info")
    void whenGettingMeWithUserIdPrincipal_thenReturns200AndUserInfo() throws Exception {
        UserId userId = new UserId(UUID.randomUUID());
        User user = new User(userId, "user@example.com", "hashed-pw", Currency.getInstance("USD"));
        when(getCurrentUserUseCase.getCurrentUser(eq(userId))).thenReturn(user);

        // Filters are disabled for the slice, so emulate what the JWT filter
        // does: place a UserId-principal authentication into the context so
        // @AuthenticationPrincipal resolves it.
        var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.value().toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.defaultCurrency").value("USD"));
    }
}
