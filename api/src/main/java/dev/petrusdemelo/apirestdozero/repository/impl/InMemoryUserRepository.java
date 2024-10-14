package dev.petrusdemelo.apirestdozero.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import dev.petrusdemelo.apirestdozero.domain.User;
import dev.petrusdemelo.apirestdozero.repository.UserRepository;

// @Repository
public class InMemoryUserRepository implements UserRepository{
  private final List<User> users = new ArrayList();

  public Optional<User> findByEmail(String email) {
    return this.users.stream()
      .filter(user -> email.equals(user.getEmail()))
      .findAny();
  }

  public User createUser(User user) {
    this.users.add(user);
    return user;
  }  

  public Optional<User> findById(UUID id) {
    return this.users.stream()
      .filter(user -> id.equals(user.getId()))
      .findAny();
  }

  public void deleteUserByID(UUID id) {
    this.users.removeIf(user -> id.equals(user.getId()));
  }

  public User save(User user) {
    this.users.removeIf(u -> user.getId().equals(u.getId()));
    this.users.add(user);
    return user;
  }
}
