package dev.petrusdemelo.apirestdozero.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User implements UserDetails{
  @Builder.Default
  private UUID id = UUID.randomUUID();
  private String name;
  private String email;
  private String password;

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @Builder.Default
  private LocalDateTime updatedAt = LocalDateTime.now();

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  public String getUsername() {
    return this.email;
  } 
}
