package dev.yuriel.fpms.adapter.in.web.dto;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Inbound web DTO for {@code PUT /api/categories/{id}}.
 *
 * @param name the new category name
 * @param type the new classification (income or expense)
 */
public record UpdateCategoryRequest(@NotBlank String name, @NotNull TransactionType type) {}
