package dev.petrusdemelo.apirestdozero.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.petrusdemelo.apirestdozero.controller.dto.AuthenticateUserRequestDTO;
import dev.petrusdemelo.apirestdozero.service.AuthenticateService;

@TestInstance(Lifecycle.PER_CLASS)
class AuthenticationControllerTest {
  private AuthenticationController authenticationController;

  @Mock private AuthenticateService service;

  @BeforeAll
  void setup() {
    MockitoAnnotations.openMocks(this);
    authenticationController = new AuthenticationController(this.service);
  }

  @BeforeEach
  void resetMocks() {
    reset(this.service);
  }

  @Test
  void itShouldReturnAccessToken() {
    // given
    var request = new AuthenticateUserRequestDTO(
      "john.doe@gmail.com", 
      "123456"
    );

    when(this.service.authenticate(request.username(), request.password()))
      .thenReturn("accessToken");

    // when
    var response = this.authenticationController.authenticate(request);
    var body = response.getBody();

    // then
    assertEquals(body.accessToken(), "accessToken");
    verify(this.service, times(1))
      .authenticate(request.username(), request.password());
  }
}
