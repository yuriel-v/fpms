package dev.yuriel.fpms.adapter.in.web;

import dev.yuriel.fpms.adapter.in.web.dto.UserResponse;
import dev.yuriel.fpms.adapter.in.web.mapper.AuthMapper;
import dev.yuriel.fpms.application.port.in.GetCurrentUserUseCase;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound web adapter exposing the current-user endpoint.
 *
 * <p>The {@link UserId} principal is placed into the security context by the
 * JWT filter and resolved here via {@link AuthenticationPrincipal}.
 */
@RestController
public class MeController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final AuthMapper authMapper;

    public MeController(GetCurrentUserUseCase getCurrentUserUseCase, AuthMapper authMapper) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.authMapper = authMapper;
    }

    @GetMapping("/api/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserId userId) {
        User user = getCurrentUserUseCase.getCurrentUser(userId);
        return ResponseEntity.ok(authMapper.toResponse(user));
    }
}
