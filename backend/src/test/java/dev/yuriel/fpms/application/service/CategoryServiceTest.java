package dev.yuriel.fpms.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.yuriel.fpms.application.port.in.CategoryNotFoundException;
import dev.yuriel.fpms.application.port.in.CreateCategoryCommand;
import dev.yuriel.fpms.application.port.in.DeleteCategoryCommand;
import dev.yuriel.fpms.application.port.in.UpdateCategoryCommand;
import dev.yuriel.fpms.application.port.out.CategoryRepository;
import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Fast unit tests for {@link CategoryService} — no Spring context, no database. Drives the outbound
 * {@link CategoryRepository} with a simple hand-written owner-aware in-memory fake.
 */
class CategoryServiceTest {

  private static final UserId OWNER = UserId.newId();
  private static final UserId OTHER = UserId.newId();

  private InMemoryCategoryRepository categories;
  private CategoryService service;

  @BeforeEach
  void setUp() {
    categories = new InMemoryCategoryRepository();
    service = new CategoryService(categories);
  }

  // ---- create ------------------------------------------------------------

  @Test
  @DisplayName("Creating a category persists it for the owner with a generated id")
  void whenCreatingCategoryThenPersistsForOwnerWithGeneratedId() {
    Category created =
        service.create(new CreateCategoryCommand(OWNER, "Salary", TransactionType.INCOME));

    assertNotNull(created.id());
    assertEquals(OWNER, created.owner());
    assertEquals("Salary", created.name());
    assertEquals(TransactionType.INCOME, created.type());
    assertTrue(categories.findByIdAndOwner(created.id(), OWNER).isPresent());
  }

  // ---- list --------------------------------------------------------------

  @Test
  @DisplayName("Listing returns only the owner's categories")
  void whenListingThenReturnsOnlyOwnersCategories() {
    service.create(new CreateCategoryCommand(OWNER, "Salary", TransactionType.INCOME));
    service.create(new CreateCategoryCommand(OWNER, "Rent", TransactionType.EXPENSE));
    service.create(new CreateCategoryCommand(OTHER, "Groceries", TransactionType.EXPENSE));

    List<Category> owned = service.list(OWNER);

    assertEquals(2, owned.size());
    assertTrue(owned.stream().allMatch(c -> c.owner().equals(OWNER)));
  }

  @Test
  @DisplayName("Listing with no categories returns an empty list")
  void whenListingWithNoneThenReturnsEmpty() {
    assertTrue(service.list(OWNER).isEmpty());
  }

  // ---- update ------------------------------------------------------------

  @Test
  @DisplayName("Updating an owned category changes its name and type, keeping id and owner")
  void whenUpdatingOwnedCategoryThenChangesNameAndType() {
    Category created =
        service.create(new CreateCategoryCommand(OWNER, "Food", TransactionType.EXPENSE));

    Category updated =
        service.update(
            new UpdateCategoryCommand(OWNER, created.id(), "Dining", TransactionType.EXPENSE));

    assertEquals(created.id(), updated.id());
    assertEquals(OWNER, updated.owner());
    assertEquals("Dining", updated.name());
    assertEquals(TransactionType.EXPENSE, updated.type());
    assertEquals("Dining", categories.findByIdAndOwner(created.id(), OWNER).orElseThrow().name());
  }

  @Test
  @DisplayName("Updating a category the user does not own is rejected")
  void whenUpdatingUnownedCategoryThenThrows() {
    Category othersCategory =
        service.create(new CreateCategoryCommand(OTHER, "Secret", TransactionType.EXPENSE));

    assertThrows(
        CategoryNotFoundException.class,
        () ->
            service.update(
                new UpdateCategoryCommand(
                    OWNER, othersCategory.id(), "Hijack", TransactionType.EXPENSE)));
  }

  @Test
  @DisplayName("Updating a non-existent category is rejected")
  void whenUpdatingMissingCategoryThenThrows() {
    assertThrows(
        CategoryNotFoundException.class,
        () ->
            service.update(
                new UpdateCategoryCommand(
                    OWNER, CategoryId.newId(), "Ghost", TransactionType.INCOME)));
  }

  // ---- delete ------------------------------------------------------------

  @Test
  @DisplayName("Deleting an owned category removes it")
  void whenDeletingOwnedCategoryThenRemovesIt() {
    Category created =
        service.create(new CreateCategoryCommand(OWNER, "Food", TransactionType.EXPENSE));

    service.delete(new DeleteCategoryCommand(OWNER, created.id()));

    assertFalse(categories.findByIdAndOwner(created.id(), OWNER).isPresent());
  }

  @Test
  @DisplayName("Deleting a category the user does not own is rejected and leaves it intact")
  void whenDeletingUnownedCategoryThenThrowsAndLeavesIntact() {
    Category othersCategory =
        service.create(new CreateCategoryCommand(OTHER, "Secret", TransactionType.EXPENSE));

    assertThrows(
        CategoryNotFoundException.class,
        () -> service.delete(new DeleteCategoryCommand(OWNER, othersCategory.id())));
    assertTrue(categories.findByIdAndOwner(othersCategory.id(), OTHER).isPresent());
  }

  @Test
  @DisplayName("Deleting a non-existent category is rejected")
  void whenDeletingMissingCategoryThenThrows() {
    assertThrows(
        CategoryNotFoundException.class,
        () -> service.delete(new DeleteCategoryCommand(OWNER, CategoryId.newId())));
  }

  // ---- fake --------------------------------------------------------------

  /** Owner-aware in-memory {@link CategoryRepository}. */
  private static final class InMemoryCategoryRepository implements CategoryRepository {
    private final List<Category> store = new ArrayList<>();

    @Override
    public Category save(Category category) {
      store.removeIf(c -> c.id().equals(category.id()) && c.owner().equals(category.owner()));
      store.add(category);
      return category;
    }

    @Override
    public Optional<Category> findByIdAndOwner(CategoryId id, UserId owner) {
      return store.stream().filter(c -> c.id().equals(id) && c.owner().equals(owner)).findFirst();
    }

    @Override
    public List<Category> findAllByOwner(UserId owner) {
      return store.stream().filter(c -> c.owner().equals(owner)).toList();
    }

    @Override
    public boolean existsByIdAndOwner(CategoryId id, UserId owner) {
      return findByIdAndOwner(id, owner).isPresent();
    }

    @Override
    public void deleteByIdAndOwner(CategoryId id, UserId owner) {
      store.removeIf(c -> c.id().equals(id) && c.owner().equals(owner));
    }
  }
}
