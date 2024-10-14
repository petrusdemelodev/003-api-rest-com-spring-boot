package dev.petrusdemelo.apirestdozero.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.petrusdemelo.apirestdozero.controller.dto.AuthenticateUserRequestDTO;
import dev.petrusdemelo.apirestdozero.controller.dto.AuthenticateUserResponseDTO;
import dev.petrusdemelo.apirestdozero.service.AuthenticateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
  private final AuthenticateService authenticateService;

  @PostMapping("/login")
  @Operation(summary = "Authenticate user")
  public ResponseEntity<AuthenticateUserResponseDTO> authenticate(
    @RequestBody @Valid AuthenticateUserRequestDTO authenticateUserRequestDTO){ 
    String accessToken = authenticateService.authenticate(
      authenticateUserRequestDTO.username(), 
      authenticateUserRequestDTO.password()
    );
    
    var response = new AuthenticateUserResponseDTO(accessToken);
    return ResponseEntity.ok(response);
  }
}
