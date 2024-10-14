package dev.petrusdemelo.apirestdozero.service;

import java.util.UUID;

import dev.petrusdemelo.apirestdozero.service.dto.CreateUserCommand;
import dev.petrusdemelo.apirestdozero.service.dto.UserDTO;

public interface UserService {
  UUID createUser(CreateUserCommand command);
  UserDTO getUserByID(UUID id);
  void deleteUserByID(UUID id);
  void updateUser(UUID id, String password);
}
