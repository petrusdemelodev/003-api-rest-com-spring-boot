package dev.petrusdemelo.apirestdozero.service.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
  public EmailAlreadyExistException() {
    super("Email already exist");
  }  
}
