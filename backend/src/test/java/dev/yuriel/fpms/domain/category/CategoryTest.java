package dev.yuriel.fpms.domain.category;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    private final CategoryId id = CategoryId.newId();
    private final UserId owner = UserId.newId();

    @Test
    void creates_a_category_with_id_owner_name_and_type() {
        Category c = Category.create(id, owner, "Salary", TransactionType.INCOME);
        assertEquals(id, c.id());
        assertEquals(owner, c.owner());
        assertEquals("Salary", c.name());
        assertEquals(TransactionType.INCOME, c.type());
    }

    @Test
    void new_category_generates_an_id_and_sets_fields() {
        Category c = Category.newCategory(owner, "Groceries", TransactionType.EXPENSE);
        assertNotNull(c.id());
        assertEquals(owner, c.owner());
        assertEquals("Groceries", c.name());
        assertEquals(TransactionType.EXPENSE, c.type());
    }

    @Test
    void trims_surrounding_whitespace_from_name() {
        Category c = Category.newCategory(owner, "  Rent  ", TransactionType.EXPENSE);
        assertEquals("Rent", c.name());
    }

    @Test
    void rejects_null_id() {
        assertThrows(NullPointerException.class,
                () -> Category.create(null, owner, "X", TransactionType.INCOME));
    }

    @Test
    void rejects_null_owner() {
        assertThrows(NullPointerException.class,
                () -> Category.create(id, null, "X", TransactionType.INCOME));
    }

    @Test
    void rejects_null_type() {
        assertThrows(NullPointerException.class,
                () -> Category.create(id, owner, "X", null));
    }

    @Test
    void rejects_null_name() {
        assertThrows(NullPointerException.class,
                () -> Category.create(id, owner, null, TransactionType.INCOME));
    }

    @Test
    void rejects_blank_name() {
        assertThrows(IllegalArgumentException.class,
                () -> Category.create(id, owner, "   ", TransactionType.INCOME));
    }

    @Test
    void renames_to_a_new_name() {
        Category c = Category.newCategory(owner, "Food", TransactionType.EXPENSE);
        Category renamed = c.rename("Dining");
        assertEquals("Dining", renamed.name());
        // original unchanged (value semantics)
        assertEquals("Food", c.name());
    }

    @Test
    void rename_rejects_blank() {
        Category c = Category.newCategory(owner, "Food", TransactionType.EXPENSE);
        assertThrows(IllegalArgumentException.class, () -> c.rename("  "));
    }
}
