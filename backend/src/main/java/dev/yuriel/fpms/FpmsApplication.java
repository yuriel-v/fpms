package dev.yuriel.fpms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Excludes {@link UserDetailsServiceAutoConfiguration}: this is a stateless, JWT-based API with no
 * username/password user store, so Boot's default in-memory user (and its generated dev password)
 * is unwanted.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class FpmsApplication {

  public static void main(String[] args) {
    SpringApplication.run(FpmsApplication.class, args);
  }
}
