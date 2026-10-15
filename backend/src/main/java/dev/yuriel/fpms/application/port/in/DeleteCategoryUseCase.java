package dev.yuriel.fpms.application.port.in;

/**
 * Inbound port for deleting a category the caller owns.
 *
 * <p>Implemented by an application service; the application depends only on this abstraction. Pure
 * Java + domain types — no framework dependencies.
 */
public interface DeleteCategoryUseCase {

  /**
   * Deletes an existing category owned by the command's owner.
   *
   * @param command the target category id and owner
   * @throws CategoryNotFoundException if the owner has no category with that id
   */
  void delete(DeleteCategoryCommand command);
}
