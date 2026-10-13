package dev.yuriel.fpms.adapter.in.web;

import dev.yuriel.fpms.adapter.in.web.dto.AuthResponse;
import dev.yuriel.fpms.adapter.in.web.dto.LoginRequest;
import dev.yuriel.fpms.adapter.in.web.dto.RegisterRequest;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.LoginUseCase;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.application.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound web adapter exposing the public authentication endpoints.
 *
 * <p>Depends only on inbound port interfaces; it maps web DTOs to commands and
 * use-case results back to responses at the web edge.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterCommand command =
                new RegisterCommand(request.email(), request.password(), request.defaultCurrency());
        AuthResponse body = AuthResponse.from(registerUserUseCase.register(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        AuthResponse body = AuthResponse.from(loginUseCase.login(command));
        return ResponseEntity.ok(body);
    }
}
