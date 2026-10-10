package dev.yuriel.fpms.domain.category;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    private final CategoryId id = CategoryId.newId();
    private final UserId owner = UserId.newId();

    @Test
    @DisplayName("Creating a category retains its id, owner, name and type")
    void whenCreatingCategory_thenRetainsIdOwnerNameAndType() {
        Category c = Category.create(id, owner, "Salary", TransactionType.INCOME);
        assertEquals(id, c.id());
        assertEquals(owner, c.owner());
        assertEquals("Salary", c.name());
        assertEquals(TransactionType.INCOME, c.type());
    }

    @Test
    @DisplayName("Creating a new category generates an id and sets its fields")
    void whenCreatingNewCategory_thenGeneratesIdAndSetsFields() {
        Category c = Category.newCategory(owner, "Groceries", TransactionType.EXPENSE);
        assertNotNull(c.id());
        assertEquals(owner, c.owner());
        assertEquals("Groceries", c.name());
        assertEquals(TransactionType.EXPENSE, c.type());
    }

    @Test
    @DisplayName("Creating a category trims surrounding whitespace from the name")
    void whenCreatingCategory_thenTrimsWhitespaceFromName() {
        Category c = Category.newCategory(owner, "  Rent  ", TransactionType.EXPENSE);
        assertEquals("Rent", c.name());
    }

    @Test
    @DisplayName("Creating a category with a null id is rejected")
    void whenCreatingCategoryWithNullId_thenThrows() {
        assertThrows(NullPointerException.class,
                () -> Category.create(null, owner, "X", TransactionType.INCOME));
    }

    @Test
    @DisplayName("Creating a category with a null owner is rejected")
    void whenCreatingCategoryWithNullOwner_thenThrows() {
        assertThrows(NullPointerException.class,
                () -> Category.create(id, null, "X", TransactionType.INCOME));
    }

    @Test
    @DisplayName("Creating a category with a null type is rejected")
    void whenCreatingCategoryWithNullType_thenThrows() {
        assertThrows(NullPointerException.class,
                () -> Category.create(id, owner, "X", null));
    }

    @Test
    @DisplayName("Creating a category with a null name is rejected")
    void whenCreatingCategoryWithNullName_thenThrows() {
        assertThrows(NullPointerException.class,
                () -> Category.create(id, owner, null, TransactionType.INCOME));
    }

    @Test
    @DisplayName("Creating a category with a blank name is rejected")
    void whenCreatingCategoryWithBlankName_thenThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Category.create(id, owner, "   ", TransactionType.INCOME));
    }

    @Test
    @DisplayName("Renaming a category returns a new instance leaving the original unchanged")
    void whenRenamingCategory_thenReturnsNewInstanceLeavingOriginalUnchanged() {
        Category c = Category.newCategory(owner, "Food", TransactionType.EXPENSE);
        Category renamed = c.rename("Dining");
        assertEquals("Dining", renamed.name());
        assertEquals("Food", c.name());
    }

    @Test
    @DisplayName("Renaming a category to a blank name is rejected")
    void whenRenamingCategoryToBlank_thenThrows() {
        Category c = Category.newCategory(owner, "Food", TransactionType.EXPENSE);
        assertThrows(IllegalArgumentException.class, () -> c.rename("  "));
    }
}
