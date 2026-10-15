/**
 * Driven (security) adapters — password hashing and JWT token issuance / validation. Implement the
 * outbound security ports without leaking the underlying libraries (BCrypt, JJWT) into the
 * application layer.
 */
package dev.yuriel.fpms.adapter.out.security;
