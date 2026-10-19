package dev.yuriel.fpms.adapter.in.web;

import dev.yuriel.fpms.domain.category.CategoryId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a path-variable {@code String} into a {@link CategoryId} so controllers can declare the
 * id directly as a domain type. A malformed UUID yields an {@link IllegalArgumentException}, which
 * the web exception handler maps to {@code 400 Bad Request}.
 */
@Component
public class CategoryIdConverter implements Converter<String, CategoryId> {

  @Override
  public CategoryId convert(String source) {
    return CategoryId.of(source);
  }
}
