package dev.yuriel.fpms.domain.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CategoryTest {

  private static final CategoryId ID = CategoryId.newId();
  private static final UserId OWNER = UserId.newId();

  @Test
  @DisplayName("Creating a category retains its id, owner, name and type")
  void whenCreatingCategoryThenRetainsIdOwnerNameAndType() {
    Category c = Category.create(ID, OWNER, "Salary", TransactionType.INCOME);
    assertEquals(ID, c.id());
    assertEquals(OWNER, c.owner());
    assertEquals("Salary", c.name());
    assertEquals(TransactionType.INCOME, c.type());
  }

  @Test
  @DisplayName("Creating a new category generates an id and sets its fields")
  void whenCreatingNewCategoryThenGeneratesIdAndSetsFields() {
    Category c = Category.newCategory(OWNER, "Groceries", TransactionType.EXPENSE);
    assertNotNull(c.id());
    assertEquals(OWNER, c.owner());
    assertEquals("Groceries", c.name());
    assertEquals(TransactionType.EXPENSE, c.type());
  }

  @Test
  @DisplayName("Creating a category trims surrounding whitespace from the name")
  void whenCreatingCategoryThenTrimsWhitespaceFromName() {
    Category c = Category.newCategory(OWNER, "  Rent  ", TransactionType.EXPENSE);
    assertEquals("Rent", c.name());
  }

  @ParameterizedTest(name = "null {0}")
  @MethodSource("creationsWithANullRequiredField")
  @DisplayName("Creating a category with any null required field is rejected")
  void whenCreatingCategoryWithNullRequiredFieldThenThrows(String field, Executable creation) {
    assertThrows(NullPointerException.class, creation);
  }

  private static Stream<Arguments> creationsWithANullRequiredField() {
    return Stream.of(
        Arguments.of(
            "id", (Executable) () -> Category.create(null, OWNER, "X", TransactionType.INCOME)),
        Arguments.of(
            "owner", (Executable) () -> Category.create(ID, null, "X", TransactionType.INCOME)),
        Arguments.of("type", (Executable) () -> Category.create(ID, OWNER, "X", null)),
        Arguments.of(
            "name", (Executable) () -> Category.create(ID, OWNER, null, TransactionType.INCOME)));
  }

  @Test
  @DisplayName("Creating a category with a blank name is rejected")
  void whenCreatingCategoryWithBlankNameThenThrows() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Category.create(ID, OWNER, "   ", TransactionType.INCOME));
  }

  @Test
  @DisplayName("Renaming a category returns a new instance leaving the original unchanged")
  void whenRenamingCategoryThenReturnsNewInstanceLeavingOriginalUnchanged() {
    Category c = Category.newCategory(OWNER, "Food", TransactionType.EXPENSE);
    Category renamed = c.rename("Dining");
    assertEquals("Dining", renamed.name());
    assertEquals("Food", c.name());
  }

  @Test
  @DisplayName("Renaming a category to a blank name is rejected")
  void whenRenamingCategoryToBlankThenThrows() {
    Category c = Category.newCategory(OWNER, "Food", TransactionType.EXPENSE);
    assertThrows(IllegalArgumentException.class, () -> c.rename("  "));
  }
}
