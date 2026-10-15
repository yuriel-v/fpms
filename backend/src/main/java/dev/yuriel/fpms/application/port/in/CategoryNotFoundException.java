package dev.yuriel.fpms.application.port.in;

/**
 * Thrown by {@link UpdateCategoryUseCase} and {@link DeleteCategoryUseCase} when the owner has no
 * category with the given identity — whether it never existed or belongs to a different user (the
 * two are indistinguishable to a caller, by design, so a user cannot probe for others' ids).
 * Unchecked so use cases need not declare it. Pure Java — no framework dependencies.
 */
public class CategoryNotFoundException extends RuntimeException {

  public CategoryNotFoundException(String message) {
    super(message);
  }

  public CategoryNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
