package dev.yuriel.fpms.domain.category;

import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;

import java.util.Objects;

/**
 * An owner-scoped classification for transactions (e.g. "Salary", "Groceries").
 * Its {@link TransactionType} marks whether it classifies income or expenses.
 *
 * <p>Immutable value: mutating operations such as {@link #rename(String)} return
 * a new instance. Pure Java — no framework dependencies.
 */
public record Category(CategoryId id, UserId owner, String name, TransactionType type) {

    public Category {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(owner, "owner must not be null");
        Objects.requireNonNull(type, "type must not be null");
        name = normalizeName(name);
    }

    /** Reconstitute a category with a known id (e.g. loaded from storage). */
    public static Category create(CategoryId id, UserId owner, String name, TransactionType type) {
        return new Category(id, owner, name, type);
    }

    /** Create a brand-new category with a freshly generated id. */
    public static Category newCategory(UserId owner, String name, TransactionType type) {
        return new Category(CategoryId.newId(), owner, name, type);
    }

    public Category rename(String newName) {
        return new Category(this.id, this.owner, newName, this.type);
    }

    private static String normalizeName(String raw) {
        Objects.requireNonNull(raw, "name must not be null");
        String trimmed = raw.strip();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return trimmed;
    }
}
