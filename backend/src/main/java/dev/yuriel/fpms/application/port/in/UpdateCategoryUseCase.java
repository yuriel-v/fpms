package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.category.Category;

/**
 * Inbound port for editing a category the caller owns.
 *
 * <p>Implemented by an application service; the application depends only on this abstraction. Pure
 * Java + domain types — no framework dependencies.
 */
public interface UpdateCategoryUseCase {

  /**
   * Updates an existing category owned by the command's owner.
   *
   * @param command the target category id, owner, and new values
   * @return the updated category
   * @throws CategoryNotFoundException if the owner has no category with that id
   */
  Category update(UpdateCategoryCommand command);
}
