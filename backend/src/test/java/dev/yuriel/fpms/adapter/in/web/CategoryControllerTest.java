package dev.yuriel.fpms.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.yuriel.fpms.adapter.in.web.dto.CategoryResponse;
import dev.yuriel.fpms.adapter.in.web.dto.CreateCategoryRequest;
import dev.yuriel.fpms.adapter.in.web.dto.UpdateCategoryRequest;
import dev.yuriel.fpms.adapter.in.web.mapper.CategoryMapperImpl;
import dev.yuriel.fpms.application.port.in.CreateCategoryCommand;
import dev.yuriel.fpms.application.port.in.CreateCategoryUseCase;
import dev.yuriel.fpms.application.port.in.DeleteCategoryCommand;
import dev.yuriel.fpms.application.port.in.DeleteCategoryUseCase;
import dev.yuriel.fpms.application.port.in.ListCategoriesUseCase;
import dev.yuriel.fpms.application.port.in.UpdateCategoryCommand;
import dev.yuriel.fpms.application.port.in.UpdateCategoryUseCase;
import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.transaction.TransactionType;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test for {@link CategoryController}: constructed directly with mocked use-case ports and a
 * real {@link CategoryMapperImpl}. Verifies the controller's own behavior —
 * request/command/response mapping, owner propagation from the principal, and HTTP status — without
 * a Spring context.
 */
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  private static final UserId OWNER = UserId.newId();

  @Mock private CreateCategoryUseCase createCategoryUseCase;
  @Mock private ListCategoriesUseCase listCategoriesUseCase;
  @Mock private UpdateCategoryUseCase updateCategoryUseCase;
  @Mock private DeleteCategoryUseCase deleteCategoryUseCase;

  private CategoryController controller() {
    return new CategoryController(
        createCategoryUseCase,
        listCategoriesUseCase,
        updateCategoryUseCase,
        deleteCategoryUseCase,
        new CategoryMapperImpl());
  }

  @Test
  @DisplayName("create maps the request with the owner and returns 201 with the category")
  void whenCreatingThenMapsOwnerAndReturns201() {
    Category created = Category.newCategory(OWNER, "Salary", TransactionType.INCOME);
    when(createCategoryUseCase.create(any(CreateCategoryCommand.class))).thenReturn(created);

    ResponseEntity<CategoryResponse> response =
        controller().create(OWNER, new CreateCategoryRequest("Salary", TransactionType.INCOME));

    ArgumentCaptor<CreateCategoryCommand> captor =
        ArgumentCaptor.forClass(CreateCategoryCommand.class);
    verify(createCategoryUseCase).create(captor.capture());
    assertEquals(OWNER, captor.getValue().owner());
    assertEquals("Salary", captor.getValue().name());
    assertEquals(TransactionType.INCOME, captor.getValue().type());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(created.id().value().toString(), response.getBody().id());
    assertEquals("Salary", response.getBody().name());
    assertEquals("INCOME", response.getBody().type());
  }

  @Test
  @DisplayName("list returns only the owner's categories as responses")
  void whenListingThenReturnsOwnersCategories() {
    Category a = Category.newCategory(OWNER, "Salary", TransactionType.INCOME);
    Category b = Category.newCategory(OWNER, "Rent", TransactionType.EXPENSE);
    when(listCategoriesUseCase.list(OWNER)).thenReturn(List.of(a, b));

    List<CategoryResponse> responses = controller().list(OWNER);

    assertEquals(2, responses.size());
    assertEquals("Salary", responses.get(0).name());
    assertEquals("Rent", responses.get(1).name());
  }

  @Test
  @DisplayName("update maps the request with the owner and id and returns the updated category")
  void whenUpdatingThenMapsOwnerAndIdAndReturnsUpdated() {
    CategoryId id = CategoryId.newId();
    Category updated = Category.create(id, OWNER, "Dining", TransactionType.EXPENSE);
    when(updateCategoryUseCase.update(any(UpdateCategoryCommand.class))).thenReturn(updated);

    CategoryResponse response =
        controller()
            .update(OWNER, id, new UpdateCategoryRequest("Dining", TransactionType.EXPENSE));

    ArgumentCaptor<UpdateCategoryCommand> captor =
        ArgumentCaptor.forClass(UpdateCategoryCommand.class);
    verify(updateCategoryUseCase).update(captor.capture());
    assertEquals(OWNER, captor.getValue().owner());
    assertEquals(id, captor.getValue().id());
    assertEquals("Dining", captor.getValue().name());

    assertEquals(id.value().toString(), response.id());
    assertEquals("Dining", response.name());
    assertEquals("EXPENSE", response.type());
  }

  @Test
  @DisplayName("delete maps the owner and id and returns 204")
  void whenDeletingThenMapsOwnerAndIdAndReturns204() {
    CategoryId id = CategoryId.newId();

    ResponseEntity<Void> response = controller().delete(OWNER, id);

    ArgumentCaptor<DeleteCategoryCommand> captor =
        ArgumentCaptor.forClass(DeleteCategoryCommand.class);
    verify(deleteCategoryUseCase).delete(captor.capture());
    assertEquals(OWNER, captor.getValue().owner());
    assertEquals(id, captor.getValue().id());

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}
