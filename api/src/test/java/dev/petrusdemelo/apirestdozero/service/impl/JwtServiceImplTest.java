package dev.petrusdemelo.apirestdozero.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import dev.petrusdemelo.apirestdozero.domain.User;
import dev.petrusdemelo.apirestdozero.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceImplTest {

    private static final String TEST_SECRET = "HLuLwodU11PHayvus2nBdE9YFwDwVSkL";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Claims claims;

    @InjectMocks
    private JwtServiceImpl jwtServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtServiceImpl, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtServiceImpl, "expiration", 1000L * 60 * 60);
    }

    @Test
    void testGenerateToken() {
        //given
        when(userDetails.getUsername()).thenReturn("testUser");

        // when
        String token = jwtServiceImpl.generateToken(userDetails);

        // then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testIsTokenValid() {
        //given
        String token = generateTestToken("testUser");        
        when(claims.getExpiration())
          .thenReturn(new Date(System.currentTimeMillis() + 1000L * 60));
        
        // when
        boolean isValid = jwtServiceImpl.isTokenValid(token);
        
        // then
        assertTrue(isValid);
    }

    @Test
    void testIsTokenInvalid() {
        // given
        String token = generateExpiredTestToken("testUser");
        
        // when        
        boolean isValid = jwtServiceImpl.isTokenValid(token);
        
        // then
        assertFalse(isValid);
    }

    @Test
    void testGetUserDetails() {
        String token = generateTestToken("testuser@gmail.com");

        User mockUserDTO = User.builder()
                            .name("Test User")
                            .email("testuser@gmail.com")  
                            .build();

        when(userRepository.findByEmail("testuser@gmail.com"))
          .thenReturn(Optional.of(mockUserDTO));

        var userDTO = jwtServiceImpl.getUser(token);

        assertNotNull(userDTO);
        assertEquals("Test User", userDTO.name());
        assertEquals("testuser@gmail.com", userDTO.email());
    }

    @Test
    void testGetUserDetailsThrowsException() {
        String token = generateTestToken("testUser");

        when(userRepository.findByEmail("testUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> jwtServiceImpl.getUser(token));
    }

    private String generateTestToken(String subject) {
        SecretKey secretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .signWith(secretKey)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60))
                .compact();
    }

    private String generateExpiredTestToken(String subject) {
      SecretKey secretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
      
      return Jwts.builder()
              .signWith(secretKey)
              .subject(subject)
              .issuedAt(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 2))
              .expiration(new Date(System.currentTimeMillis() - 1000L * 60 * 60))
              .compact();
  }
}
