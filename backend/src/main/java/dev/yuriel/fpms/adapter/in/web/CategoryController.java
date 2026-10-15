package dev.yuriel.fpms.adapter.in.web;

import dev.yuriel.fpms.adapter.in.web.dto.CategoryResponse;
import dev.yuriel.fpms.adapter.in.web.dto.CreateCategoryRequest;
import dev.yuriel.fpms.adapter.in.web.dto.UpdateCategoryRequest;
import dev.yuriel.fpms.adapter.in.web.mapper.CategoryMapper;
import dev.yuriel.fpms.application.port.in.CreateCategoryUseCase;
import dev.yuriel.fpms.application.port.in.DeleteCategoryUseCase;
import dev.yuriel.fpms.application.port.in.ListCategoriesUseCase;
import dev.yuriel.fpms.application.port.in.UpdateCategoryUseCase;
import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.user.UserId;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound web adapter exposing the authenticated user's category CRUD endpoints.
 *
 * <p>Depends only on inbound port interfaces. The owning {@link UserId} is resolved from the
 * security context via {@link AuthenticationPrincipal} and passed into every command, so each
 * operation is scoped to the caller.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CreateCategoryUseCase createCategoryUseCase;
  private final ListCategoriesUseCase listCategoriesUseCase;
  private final UpdateCategoryUseCase updateCategoryUseCase;
  private final DeleteCategoryUseCase deleteCategoryUseCase;
  private final CategoryMapper categoryMapper;

  @PostMapping
  public ResponseEntity<CategoryResponse> create(
      @AuthenticationPrincipal UserId owner, @Valid @RequestBody CreateCategoryRequest request) {
    Category created = createCategoryUseCase.create(categoryMapper.toCreateCommand(request, owner));
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(created));
  }

  @GetMapping
  public List<CategoryResponse> list(@AuthenticationPrincipal UserId owner) {
    return listCategoriesUseCase.list(owner).stream().map(categoryMapper::toResponse).toList();
  }

  @PutMapping("/{id}")
  public CategoryResponse update(
      @AuthenticationPrincipal UserId owner,
      @PathVariable("id") CategoryId id,
      @Valid @RequestBody UpdateCategoryRequest request) {
    Category updated =
        updateCategoryUseCase.update(categoryMapper.toUpdateCommand(request, owner, id));
    return categoryMapper.toResponse(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal UserId owner, @PathVariable("id") CategoryId id) {
    deleteCategoryUseCase.delete(categoryMapper.toDeleteCommand(owner, id));
    return ResponseEntity.noContent().build();
  }
}
