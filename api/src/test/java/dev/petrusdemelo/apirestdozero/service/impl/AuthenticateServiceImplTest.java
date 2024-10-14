package dev.petrusdemelo.apirestdozero.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import dev.petrusdemelo.apirestdozero.service.JwtService;

@TestInstance(Lifecycle.PER_CLASS)
class AuthenticateServiceImplTest {
  private AuthenticateServiceImpl authenticateServiceImpl;

  @Mock private AuthenticationManager authenticationManager;
  @Mock private JwtService jwtService;
  @Mock private Authentication authentication;

  @BeforeAll
  void setup() {
    MockitoAnnotations.openMocks(this);

    this.authenticateServiceImpl = new AuthenticateServiceImpl(
      this.authenticationManager, 
      this.jwtService
    );
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(this.authenticationManager);
    Mockito.reset(this.jwtService);
  }

  @Test
  void itShouldAuthenticateUserAndGenerateToken() {
    // given
    var username = "john.doe@gmail.com";
    var password = "123456";

    when(this.authenticationManager.authenticate(any()))
      .thenReturn(authentication);

    when(this.jwtService.generateToken(any()))
      .thenReturn("accessToken");

    // when
    var result = this.authenticateServiceImpl.authenticate(username, password);

    // then
    assertEquals("accessToken", result);

    verify(this.authenticationManager, times(1))
      .authenticate(any());
    
    verify(this.jwtService, times(1)).generateToken(any());
  }
}
