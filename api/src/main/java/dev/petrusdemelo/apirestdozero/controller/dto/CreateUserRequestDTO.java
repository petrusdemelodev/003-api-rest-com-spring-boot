package dev.petrusdemelo.apirestdozero.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record CreateUserRequestDTO(
    @Schema(description = "User name")
    @NotEmpty()
    String name,

    @Schema(description = "User email")
    @NotEmpty()
    String email,

    @Schema(description = "User password")
    @NotEmpty()
    String password
) {}
