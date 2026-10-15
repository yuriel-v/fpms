package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.category.Category;

/**
 * Inbound port for creating a category owned by a user.
 *
 * <p>Implemented by an application service; the application depends only on this abstraction. Pure
 * Java + domain types — no framework dependencies.
 */
public interface CreateCategoryUseCase {

  /**
   * Creates a new category for the command's owner.
   *
   * @param command the category details
   * @return the created category, with its generated identity
   */
  Category create(CreateCategoryCommand command);
}
