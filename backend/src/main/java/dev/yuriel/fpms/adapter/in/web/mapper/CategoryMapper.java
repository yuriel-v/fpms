package dev.yuriel.fpms.adapter.in.web.mapper;

import dev.yuriel.fpms.adapter.in.web.dto.CategoryResponse;
import dev.yuriel.fpms.adapter.in.web.dto.CreateCategoryRequest;
import dev.yuriel.fpms.adapter.in.web.dto.UpdateCategoryRequest;
import dev.yuriel.fpms.application.port.in.CreateCategoryCommand;
import dev.yuriel.fpms.application.port.in.DeleteCategoryCommand;
import dev.yuriel.fpms.application.port.in.UpdateCategoryCommand;
import dev.yuriel.fpms.domain.category.Category;
import dev.yuriel.fpms.domain.category.CategoryId;
import dev.yuriel.fpms.domain.user.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps the category feature's web DTOs to inbound-port commands and domain results back to response
 * DTOs.
 *
 * <p>The authenticated {@link UserId} is resolved in the controller and passed in as the owner,
 * since it is never part of a request body. MapStruct generates the straightforward field copies;
 * only the conversions it cannot derive ({@link CategoryId} to {@code String}) are supplied as
 * default methods.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CreateCategoryCommand toCreateCommand(CreateCategoryRequest request, UserId owner);

  UpdateCategoryCommand toUpdateCommand(UpdateCategoryRequest request, UserId owner, CategoryId id);

  DeleteCategoryCommand toDeleteCommand(UserId owner, CategoryId id);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "type", source = "type")
  CategoryResponse toResponse(Category category);

  default String map(CategoryId id) {
    return id == null ? null : id.value().toString();
  }
}
