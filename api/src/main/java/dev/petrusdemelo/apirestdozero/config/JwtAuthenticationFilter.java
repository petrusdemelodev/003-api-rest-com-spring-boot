package dev.petrusdemelo.apirestdozero.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import dev.petrusdemelo.apirestdozero.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final HandlerExceptionResolver handlerExceptionResolver;
  private final JwtService jwtService;

  protected void doFilterInternal(
    HttpServletRequest request, 
    HttpServletResponse response, 
    FilterChain filterChain)
      throws ServletException, IOException {
    try {
      var authorizationHeader = request.getHeader("Authorization");

      if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      var token = authorizationHeader.substring(7);

      if(jwtService.isTokenValid(token)) {
        var user = jwtService.getUser(token);

        var authToken = new UsernamePasswordAuthenticationToken(
          user,
          null,
          Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }

      filterChain.doFilter(request, response);
    } catch (Exception e){
      SecurityContextHolder.clearContext();
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }
  
}
