package dev.petrusdemelo.apirestdozero.controller.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserResponseDTO(
  @Schema(description = "User ID")
  UUID id
) {}
