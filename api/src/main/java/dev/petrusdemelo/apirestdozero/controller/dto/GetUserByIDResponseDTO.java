package dev.petrusdemelo.apirestdozero.controller.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserByIDResponseDTO(
    @Schema(description = "User ID")
    UUID id,

    @Schema(description = "Name of the user")
    String name,

    @Schema(description = "Email of the user")
    String email,

    @Schema(description = "Creation date of the user")
    String createdAt,

    @Schema(description = "Update date of the user")
    String updatedAt
) {}
