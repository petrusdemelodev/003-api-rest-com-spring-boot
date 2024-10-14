package dev.petrusdemelo.apirestdozero.repository;

import java.util.Optional;
import java.util.UUID;

import dev.petrusdemelo.apirestdozero.domain.User;

public interface UserRepository {
  Optional<User> findByEmail(String email);
  Optional<User> findById(UUID id);
  User createUser(User user);
  void deleteUserByID(UUID id);
  User save(User user);
}
