package dev.yuriel.fpms.config;

import dev.yuriel.fpms.adapter.in.web.security.JwtAuthenticationFilter;
import dev.yuriel.fpms.application.port.out.PasswordHasher;
import dev.yuriel.fpms.application.port.out.TokenService;
import dev.yuriel.fpms.application.port.out.UserRepository;
import dev.yuriel.fpms.application.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security wiring for the strict-hexagonal stack.
 *
 * <p>Stateless, token-based security: no sessions, no CSRF. The JWT filter runs
 * before the username/password filter and populates the security context from a
 * bearer token. Public routes (auth + health) are permitted; everything else
 * requires authentication, and a missing/invalid token yields a bare 401 via
 * the configured entry point.
 *
 * <p>This class also wires the framework-free {@link AuthService} into a Spring
 * bean. A single {@code AuthService} bean implements all three inbound use-case
 * interfaces, so it satisfies every use-case injection point.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, TokenService tokenService) throws Exception {
        AuthenticationEntryPoint entryPoint =
                (request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .addFilterBefore(
                        new JwtAuthenticationFilter(tokenService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Wires the framework-free {@link AuthService} as a bean. Because it
     * implements {@code RegisterUserUseCase}, {@code LoginUseCase}, and
     * {@code GetCurrentUserUseCase}, this one bean satisfies all three
     * use-case injection points.
     */
    @Bean
    public AuthService authService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenService tokenService) {
        return new AuthService(userRepository, passwordHasher, tokenService);
    }
}
