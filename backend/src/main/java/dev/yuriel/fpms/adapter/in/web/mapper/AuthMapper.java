package dev.yuriel.fpms.adapter.in.web.mapper;

import dev.yuriel.fpms.adapter.in.web.dto.AuthResponse;
import dev.yuriel.fpms.adapter.in.web.dto.LoginRequest;
import dev.yuriel.fpms.adapter.in.web.dto.RegisterRequest;
import dev.yuriel.fpms.adapter.in.web.dto.UserResponse;
import dev.yuriel.fpms.application.port.in.AuthResult;
import dev.yuriel.fpms.application.port.in.LoginCommand;
import dev.yuriel.fpms.application.port.in.RegisterCommand;
import dev.yuriel.fpms.domain.user.User;
import dev.yuriel.fpms.domain.user.UserId;
import java.util.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps the authentication feature's web DTOs to inbound-port commands and use-case results back to
 * response DTOs.
 *
 * <p>MapStruct generates the straightforward field copies; only the conversions it cannot derive on
 * its own ({@link UserId}/{@link Currency} to {@code String}) are supplied as default methods
 * below.
 */
@Mapper(componentModel = "spring")
public interface AuthMapper {

  @Mapping(target = "rawPassword", source = "password")
  @Mapping(target = "defaultCurrencyCode", source = "defaultCurrency")
  RegisterCommand toCommand(RegisterRequest request);

  @Mapping(target = "rawPassword", source = "password")
  LoginCommand toCommand(LoginRequest request);

  @Mapping(target = "userId", source = "userId")
  AuthResponse toResponse(AuthResult result);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "defaultCurrency", source = "defaultCurrency")
  UserResponse toResponse(User user);

  default String map(UserId userId) {
    return userId == null ? null : userId.value().toString();
  }

  default String map(Currency currency) {
    return currency == null ? null : currency.getCurrencyCode();
  }
}
