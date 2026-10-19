package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;

/**
 * Input for {@link CreateCategoryUseCase#create(CreateCategoryCommand)} — the details for a new
 * owner-scoped category. Pure Java + domain types — no framework dependencies.
 *
 * @param owner the user that will own the category
 * @param name the category name
 * @param type whether the category classifies income or expense
 */
public record CreateCategoryCommand(UserId owner, String name, TransactionType type) {}
