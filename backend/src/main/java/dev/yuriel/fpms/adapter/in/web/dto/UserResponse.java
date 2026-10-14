package dev.yuriel.fpms.adapter.in.web.dto;

/**
 * Inbound web DTO describing the currently authenticated user.
 *
 * @param id              the user's id (string form of the UUID)
 * @param email           the user's login email
 * @param defaultCurrency the user's default currency ISO 4217 code
 */
public record UserResponse(String id, String email, String defaultCurrency) {
}
