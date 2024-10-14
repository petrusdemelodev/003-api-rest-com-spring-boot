package dev.petrusdemelo.apirestdozero.service;

import org.springframework.security.core.userdetails.UserDetails;

import dev.petrusdemelo.apirestdozero.service.dto.UserDTO;

public interface JwtService {
  String generateToken(UserDetails userDetails);
  boolean isTokenValid(String token);
  UserDTO getUser(String token);
}
