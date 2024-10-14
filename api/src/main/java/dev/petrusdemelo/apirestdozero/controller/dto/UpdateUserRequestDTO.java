package dev.petrusdemelo.apirestdozero.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record UpdateUserRequestDTO(
  @Schema(description = "New Password", example = "123456", required = true)
  @NotEmpty
  String password
) {}
