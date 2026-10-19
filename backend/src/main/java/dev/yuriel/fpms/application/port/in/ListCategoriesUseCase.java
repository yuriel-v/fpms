package dev.yuriel.fpms.application.port.in;

import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.List;

/**
 * Inbound port for listing the categories owned by a user.
 *
 * <p>Implemented by an application service; the application depends only on this abstraction. Pure
 * Java + domain types — no framework dependencies.
 */
public interface ListCategoriesUseCase {

  /**
   * Lists the categories owned by the given user.
   *
   * @param owner the owning user
   * @return the owner's categories (empty if none)
   */
  List<Category> list(UserId owner);
}
