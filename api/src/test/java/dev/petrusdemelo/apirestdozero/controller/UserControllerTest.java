package dev.petrusdemelo.apirestdozero.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import dev.petrusdemelo.apirestdozero.controller.dto.CreateUserRequestDTO;
import dev.petrusdemelo.apirestdozero.controller.dto.UpdateUserRequestDTO;
import dev.petrusdemelo.apirestdozero.service.UserService;
import dev.petrusdemelo.apirestdozero.service.dto.CreateUserCommand;
import dev.petrusdemelo.apirestdozero.service.dto.UserDTO;
import io.jsonwebtoken.lang.Collections;

@TestInstance(Lifecycle.PER_CLASS)
class UserControllerTest {
  private UserController userController;

  @Mock private UserService userService;

  @BeforeAll
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.userController = new UserController(this.userService);
  }

  @BeforeEach
  void setUpEach() {
    reset(this.userService);
  }

  @Test
  void itShouldCallServiceAndReturnCreatedUserID() {
    // given
    var request = new CreateUserRequestDTO(
      "John Doe", 
      "john.doe@gmail.com", 
      "123456"
    );

    when(this.userService.createUser(any()))
      .thenReturn(UUID.randomUUID());

    // when
    var response = this.userController.createUser(request);
    var body = response.getBody();
    
    // then
    assertNotNull(body.id());

    var captor = ArgumentCaptor.forClass(CreateUserCommand.class);

    verify(this.userService, times(1))
      .createUser(captor.capture());

    var command = captor.getValue();
    assertEquals(command.email(), request.email());
    assertEquals(command.name(), request.name());
    assertEquals(command.password(), request.password());
  }

  @Test
  void itShouldReturnUserByID() {
    // given
    var uuid = UUID.randomUUID();
    
    var userDTO = new UserDTO(
      uuid, 
      "John Doe", 
      "john.doe@gmail.com", 
      LocalDateTime.now(), 
      LocalDateTime.now()
    );

    when(this.userService.getUserByID(uuid))
      .thenReturn(userDTO);

    // when
    var response = this.userController.getUserByID(uuid);
    var body = response.getBody();

    // then
    assertEquals(body.id(), userDTO.id());
    assertEquals(body.name(), userDTO.name());
    assertEquals(body.email(), userDTO.email());
    assertEquals(body.createdAt(), userDTO.createdAt().toString());
    assertEquals(body.updatedAt(), userDTO.updatedAt().toString());

    verify(this.userService, times(1))
      .getUserByID(uuid);
  }

  @Test
  void itShouldDeleteUserByID(){
    // given
    var uuid = UUID.randomUUID();

    // when
    this.userController.deleteUserByID(uuid);

    // then
    verify(this.userService, times(1))
      .deleteUserByID(uuid);
  }

  @Test
  void itShouldUpdateUserByID(){
    // given
    var uuid = UUID.randomUUID();
    var request = new UpdateUserRequestDTO("123456");
    
    // when
    this.userController.updateUser(uuid, request);

    // then
    verify(this.userService, times(1))
      .updateUser(uuid, request.password());
  }

  @Test
  void itShouldReturnAuthenticatedUser(){
    // given
    var userDTO = new UserDTO(
      UUID.randomUUID(), 
      "John Doe", 
      "john.doe@gmail.com", 
      LocalDateTime.now(), 
      LocalDateTime.now()
    );

    var authToken = new UsernamePasswordAuthenticationToken(
      userDTO,
      null,
      Collections.emptyList()
    );

    SecurityContextHolder.getContext().setAuthentication(authToken);

    // when
    var response = this.userController.getAuthenticatedUser();
    var body = response.getBody();

    // then
    assertEquals(body.id(), userDTO.id());
    assertEquals(body.name(), userDTO.name());
    assertEquals(body.email(), userDTO.email());
    assertEquals(body.createdAt(), userDTO.createdAt().toString());
    assertEquals(body.updatedAt(), userDTO.updatedAt().toString());
  }
}
