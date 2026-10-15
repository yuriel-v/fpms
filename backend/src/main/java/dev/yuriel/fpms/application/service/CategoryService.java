package dev.yuriel.fpms.application.service;

import dev.yuriel.fpms.application.port.in.CategoryNotFoundException;
import dev.yuriel.fpms.application.port.in.CreateCategoryCommand;
import dev.yuriel.fpms.application.port.in.CreateCategoryUseCase;
import dev.yuriel.fpms.application.port.in.DeleteCategoryCommand;
import dev.yuriel.fpms.application.port.in.DeleteCategoryUseCase;
import dev.yuriel.fpms.application.port.in.ListCategoriesUseCase;
import dev.yuriel.fpms.application.port.in.UpdateCategoryCommand;
import dev.yuriel.fpms.application.port.in.UpdateCategoryUseCase;
import dev.yuriel.fpms.application.port.out.CategoryRepository;
import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.List;
import java.util.Objects;

/**
 * Application service implementing the category use cases — create, list, update, and delete.
 *
 * <p>Every operation is owner-scoped: updates and deletes resolve the target through the
 * owner-aware {@link CategoryRepository} first, so a user can only ever affect their own
 * categories; a miss (absent or owned by someone else) raises {@link CategoryNotFoundException}.
 *
 * <p>Deliberately framework-free: wiring into a Spring bean lives in the config layer, keeping this
 * layer pure and fast to unit-test.
 */
public class CategoryService
    implements CreateCategoryUseCase,
        ListCategoriesUseCase,
        UpdateCategoryUseCase,
        DeleteCategoryUseCase {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository =
        Objects.requireNonNull(categoryRepository, "categoryRepository must not be null");
  }

  @Override
  public Category create(CreateCategoryCommand command) {
    Category category = Category.newCategory(command.owner(), command.name(), command.type());
    return categoryRepository.save(category);
  }

  @Override
  public List<Category> list(UserId owner) {
    return categoryRepository.findAllByOwner(owner);
  }

  @Override
  public Category update(UpdateCategoryCommand command) {
    Category existing =
        categoryRepository
            .findByIdAndOwner(command.id(), command.owner())
            .orElseThrow(
                () -> new CategoryNotFoundException("no category exists for the given id"));

    Category updated =
        Category.create(existing.id(), existing.owner(), command.name(), command.type());
    return categoryRepository.save(updated);
  }

  @Override
  public void delete(DeleteCategoryCommand command) {
    if (!categoryRepository.existsByIdAndOwner(command.id(), command.owner())) {
      throw new CategoryNotFoundException("no category exists for the given id");
    }
    categoryRepository.deleteByIdAndOwner(command.id(), command.owner());
  }
}
