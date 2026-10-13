package dev.yuriel.fpms.adapter.in.web.dto;

/**
 * Minimal, consistent error body returned by the web adapter.
 *
 * @param message a short, human-readable description of the error
 */
public record ErrorResponse(String message) {
}
