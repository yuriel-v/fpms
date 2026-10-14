package dev.yuriel.fpms.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import dev.yuriel.fpms.adapter.in.web.dto.UserResponse;
import dev.yuriel.fpms.adapter.in.web.mapper.AuthMapperImpl;
import dev.yuriel.fpms.application.port.in.GetCurrentUserUseCase;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test for {@link MeController}: constructed directly with a mocked use-case port and a real
 * {@link AuthMapperImpl}. Verifies the controller resolves the current user for the given principal
 * and maps it to the response with a 200 status. No Spring context; the principal is passed as a
 * method argument (as {@code @AuthenticationPrincipal} would supply it).
 */
@ExtendWith(MockitoExtension.class)
class MeControllerTest {

  @Mock private GetCurrentUserUseCase getCurrentUserUseCase;

  @Test
  @DisplayName("me resolves the current user and returns 200 with the user info")
  void whenGettingMe_thenReturns200AndUserInfo() {
    UserId userId = UserId.of(UUID.randomUUID());
    User user = User.create(userId, "user@example.com", "hashed-pw", "USD");
    when(getCurrentUserUseCase.getCurrentUser(userId)).thenReturn(user);

    MeController controller = new MeController(getCurrentUserUseCase, new AuthMapperImpl());
    ResponseEntity<UserResponse> response = controller.me(userId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(userId.value().toString(), response.getBody().id());
    assertEquals("user@example.com", response.getBody().email());
    assertEquals("USD", response.getBody().defaultCurrency());
  }
}
