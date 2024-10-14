package dev.petrusdemelo.apirestdozero.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticateUserResponseDTO(
  @Schema(
    description = "Access token", 
    example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyNjMwNjQwMH0"
  )
  String accessToken
) {}
