package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;

/**
 * Input for {@link UpdateCategoryUseCase#update(UpdateCategoryCommand)} — identifies the category
 * to edit (scoped to its owner) and carries the new values. Pure Java + domain types — no framework
 * dependencies.
 *
 * @param owner the user that must own the category
 * @param id the identity of the category to update
 * @param name the new category name
 * @param type the new classification (income or expense)
 */
public record UpdateCategoryCommand(
    UserId owner, CategoryId id, String name, TransactionType type) {}
