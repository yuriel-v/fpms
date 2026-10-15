package dev.yuriel.fpms.adapter.in.web.dto;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Inbound web DTO for {@code POST /api/categories}.
 *
 * @param name the category name
 * @param type whether the category classifies income or expense
 */
public record CreateCategoryRequest(@NotBlank String name, @NotNull TransactionType type) {}
