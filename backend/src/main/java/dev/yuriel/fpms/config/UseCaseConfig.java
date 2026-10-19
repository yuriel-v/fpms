package dev.yuriel.fpms.config;

import dev.yuriel.fpms.application.port.out.CategoryRepository;
import dev.yuriel.fpms.application.service.CategoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the framework-free application services into Spring beans, keeping the application layer
 * itself free of framework annotations.
 */
@Configuration
public class UseCaseConfig {

  /**
   * Wires the framework-free {@link CategoryService} as a bean. Because it implements all four
   * category use-case interfaces, this one bean satisfies every category use-case injection point.
   */
  @Bean
  public CategoryService categoryService(CategoryRepository categoryRepository) {
    return new CategoryService(categoryRepository);
  }
}
