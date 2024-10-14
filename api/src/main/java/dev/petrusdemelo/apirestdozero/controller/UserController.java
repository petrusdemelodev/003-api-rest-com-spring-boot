package dev.petrusdemelo.apirestdozero.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.petrusdemelo.apirestdozero.controller.dto.CreateUserRequestDTO;
import dev.petrusdemelo.apirestdozero.controller.dto.CreateUserResponseDTO;
import dev.petrusdemelo.apirestdozero.controller.dto.GetUserByIDResponseDTO;
import dev.petrusdemelo.apirestdozero.controller.dto.UpdateUserRequestDTO;
import dev.petrusdemelo.apirestdozero.service.JwtService;
import dev.petrusdemelo.apirestdozero.service.UserService;
import dev.petrusdemelo.apirestdozero.service.dto.CreateUserCommand;
import dev.petrusdemelo.apirestdozero.service.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
  private final UserService userService;

  @PostMapping
  @Operation(summary = "Create a new user")
  public ResponseEntity<CreateUserResponseDTO> createUser(
    @Valid @RequestBody CreateUserRequestDTO createUserRequestDTO) {
    
    var command = new CreateUserCommand(
      createUserRequestDTO.name(), 
      createUserRequestDTO.email(), 
      createUserRequestDTO.password()
    );

    var userID = this.userService.createUser(command);

    var response = new CreateUserResponseDTO(userID);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID")
  public ResponseEntity<GetUserByIDResponseDTO> getUserByID(@PathVariable UUID id) {
    var userDTO = this.userService.getUserByID(id);

    var response = new GetUserByIDResponseDTO(
      userDTO.id(),
      userDTO.name(),
      userDTO.email(),
      userDTO.createdAt().toString(),
      userDTO.updatedAt().toString()
    );

    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Operation(summary = "Get authenticated user")
  public ResponseEntity<GetUserByIDResponseDTO> getAuthenticatedUser() {
    var userDTO = (UserDTO) SecurityContextHolder.getContext()
      .getAuthentication().getPrincipal();

    var response = new GetUserByIDResponseDTO(
      userDTO.id(),
      userDTO.name(),
      userDTO.email(),
      userDTO.createdAt().toString(),
      userDTO.updatedAt().toString()
    );

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user by ID")
  public ResponseEntity<Void> deleteUserByID(@PathVariable UUID id) {
    this.userService.deleteUserByID(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update user by ID")
  public ResponseEntity<Void> updateUser(
    @PathVariable UUID id, 
    @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
    this.userService.updateUser(id, updateUserRequestDTO.password());
    return ResponseEntity.noContent().build();
  }
}
