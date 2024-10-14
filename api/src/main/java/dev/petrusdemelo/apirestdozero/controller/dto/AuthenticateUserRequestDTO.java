package dev.petrusdemelo.apirestdozero.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record AuthenticateUserRequestDTO(
  @Schema(
    description = "Username", 
    example = "admin"
  )
  @NotEmpty
  String username,

  @Schema(
    description = "password", 
    example = "123456"
  )
  @NotEmpty
  String password
) {}
