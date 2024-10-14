package dev.petrusdemelo.apirestdozero.service.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.petrusdemelo.apirestdozero.domain.User;
import dev.petrusdemelo.apirestdozero.repository.UserRepository;
import dev.petrusdemelo.apirestdozero.service.dto.CreateUserCommand;
import dev.petrusdemelo.apirestdozero.service.exceptions.EmailAlreadyExistException;
import dev.petrusdemelo.apirestdozero.service.exceptions.UserNotFoundException;

@TestInstance(Lifecycle.PER_CLASS)
class UserServiceImplTest {
  private UserServiceImpl service;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder encoder;

  @BeforeAll
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new UserServiceImpl(userRepository, encoder);
  }

  @BeforeEach
  void setUpEach() {
    reset(this.encoder, this.userRepository);
  }

  @Test
  void itShouldCreateTheUserIfEmailIsAvailable() {
    // given
    var command = new CreateUserCommand("John Doe", "john.doe@gmail.com", "123456");
    
    when(this.encoder.encode(command.password()))
      .thenReturn("encodedPassword");

    when(this.userRepository.findByEmail(command.email()))
      .thenReturn(Optional.empty());

    when(this.userRepository.createUser(any(User.class)))
      .thenAnswer(invoke -> {
        var user = invoke.getArgument(0, User.class);
        return user;
      });

    // when
    var userID = service.createUser(command);

    // then
    assertNotNull(userID);
    verify(this.encoder, times(1))
      .encode(command.password());
      
    verify(this.userRepository, times(1))
      .findByEmail(command.email());

    var captor = ArgumentCaptor.forClass(User.class);
      
    verify(this.userRepository, times(1))
      .createUser(captor.capture());

    var createdUser = captor.getValue();
    assertEquals(createdUser.getPassword(), "encodedPassword");
    assertEquals(createdUser.getId(), userID);    
  }

  @Test
  void itShouldThrowAnErrorIfEmailIsAlreadyInUse() {
    // given
    var command = new CreateUserCommand("John Doe", "john.doe@gmail.com", "123456");
  
    when(this.userRepository.findByEmail(command.email()))
      .thenReturn(Optional.of(User.builder().build()));

    // when & then
    assertThrows(EmailAlreadyExistException.class, 
      () -> service.createUser(command)
    );

    verify(this.encoder, times(0))
      .encode(any());
      
    verify(this.userRepository, times(1))
      .findByEmail(command.email());
      
    verify(this.userRepository, times(0))
      .createUser(any());
  }

  @Test
  void itShouldReturnUserDTOByID() {
    // given
    var uuid = UUID.randomUUID();

    var user = User.builder()
      .id(uuid)
      .name("John Doe")
      .email("john.doe@gmail.com")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    when(this.userRepository.findById(uuid))
      .thenReturn(Optional.of(user));

    // when
    var userDTO = this.service.getUserByID(uuid);

    // then
    assertEquals(userDTO.id(), user.getId());
    assertEquals(userDTO.name(), user.getName());
    assertEquals(userDTO.email(), user.getEmail());
    assertEquals(userDTO.createdAt(), user.getCreatedAt());
    assertEquals(userDTO.updatedAt(), user.getUpdatedAt());

    verify(this.userRepository, times(1))
      .findById(uuid);
  }

  @Test
  void itShouldReturnAnExceptionWhenUserDoesnotExist() {
    // given
    var uuid = UUID.randomUUID();
      
    when(this.userRepository.findById(uuid))
      .thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, 
      () -> this.service.getUserByID(uuid)
    );
    verify(this.userRepository, times(1))
      .findById(uuid);
  }

  @Test
  void itShouldDeleteUserByID() {
    // given
    var uuid = UUID.randomUUID();
    doNothing().when(this.userRepository)
      .deleteUserByID(uuid);

    // when
    this.service.deleteUserByID(uuid);

    // then
    verify(this.userRepository, times(1))
      .deleteUserByID(uuid);
  }

  @Test
  void itShouldUpdateUserByID(){
    // given
    var uuid = UUID.randomUUID();
    var password = "newPassword";

    var user = User.builder()
      .id(uuid)
      .name("John Doe")
      .email("john.doe@gmail.com")
      .password("123456")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    when(this.userRepository.findById(uuid))
      .thenReturn(Optional.of(user));

    when(this.encoder.encode(password))
      .thenReturn("encodedUpdatedPassword");

    when(this.userRepository.save(any(User.class)))
      .thenAnswer(invoke -> {
        var userArg = invoke.getArgument(0, User.class);
        return userArg;
      });

    // when
    this.service.updateUser(uuid, password);

    // then
    verify(this.userRepository, times(1))
      .findById(uuid);
    verify(this.encoder, times(1))
      .encode(password);
    
    var captor = ArgumentCaptor.forClass(User.class);
    verify(this.userRepository, times(1))
      .save(captor.capture());
    
    var updatedUser = captor.getValue();
    assertEquals(updatedUser.getPassword(), "encodedUpdatedPassword");
  }

  @Test
  void itShouldThrownAnErrorIfUserDoesNotExistWhenUpdating(){
    // given
    var uuid = UUID.randomUUID();
    var password = "123456";
    
    when(this.userRepository.findById(uuid))
      .thenReturn(Optional.empty());
      
    // when & then
    assertThrows(UserNotFoundException.class, 
      () -> this.service.updateUser(uuid, password)
    );
    verify(this.userRepository, times(1))
      .findById(uuid);
    verify(this.userRepository, times(0))
      .save(any());
  }
}
