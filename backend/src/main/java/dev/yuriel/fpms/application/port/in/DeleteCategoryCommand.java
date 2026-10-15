package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.user.UserId;

/**
 * Input for {@link DeleteCategoryUseCase#delete(DeleteCategoryCommand)} — identifies the category
 * to remove, scoped to its owner. Pure Java + domain types — no framework dependencies.
 *
 * @param owner the user that must own the category
 * @param id the identity of the category to delete
 */
public record DeleteCategoryCommand(UserId owner, CategoryId id) {}
