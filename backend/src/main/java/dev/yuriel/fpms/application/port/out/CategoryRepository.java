package dev.yuriel.fpms.application.port.out;

import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for category persistence — the application's view of where {@link Category}
 * aggregates are stored and retrieved.
 *
 * <p>Every operation is <strong>owner-scoped</strong>: a category is only ever read, changed, or
 * removed in the context of the {@link UserId} that owns it, so one user can never reach another's
 * categories. The driven (persistence) adapter implements this; the application depends only on
 * this abstraction. Pure Java + domain types — no framework dependencies.
 */
public interface CategoryRepository {

  /**
   * Persists a new or updated category.
   *
   * @param category the category to store
   * @return the stored category
   */
  Category save(Category category);

  /**
   * Finds a category by identity, scoped to its owner.
   *
   * @param id the category identity to look up
   * @param owner the user that must own the category
   * @return the matching category, or empty if none exists for that owner
   */
  Optional<Category> findByIdAndOwner(CategoryId id, UserId owner);

  /**
   * Lists all categories owned by the given user.
   *
   * @param owner the owning user
   * @return the owner's categories (empty if none)
   */
  List<Category> findAllByOwner(UserId owner);

  /**
   * Reports whether a category with the given id exists for the given owner.
   *
   * @param id the category identity to check
   * @param owner the user that must own the category
   * @return {@code true} if the owner has a category with this id
   */
  boolean existsByIdAndOwner(CategoryId id, UserId owner);

  /**
   * Deletes a category by identity, scoped to its owner. A no-op if the owner has no such category.
   *
   * @param id the category identity to delete
   * @param owner the user that must own the category
   */
  void deleteByIdAndOwner(CategoryId id, UserId owner);
}
