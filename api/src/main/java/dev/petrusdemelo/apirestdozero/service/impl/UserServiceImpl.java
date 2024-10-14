package dev.petrusdemelo.apirestdozero.service.impl;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.petrusdemelo.apirestdozero.domain.User;
import dev.petrusdemelo.apirestdozero.repository.UserRepository;
import dev.petrusdemelo.apirestdozero.service.UserService;
import dev.petrusdemelo.apirestdozero.service.dto.CreateUserCommand;
import dev.petrusdemelo.apirestdozero.service.dto.UserDTO;
import dev.petrusdemelo.apirestdozero.service.exceptions.EmailAlreadyExistException;
import dev.petrusdemelo.apirestdozero.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UUID createUser(CreateUserCommand command) {
        var userOptional = this.userRepository.findByEmail(command.email());

        if(userOptional.isPresent()) {
            throw new EmailAlreadyExistException();
        }

        var passwordEncoded = this.encoder.encode(command.password());

        var user = User.builder()
            .name(command.name())
            .email(command.email())
            .password(passwordEncoded)
            .build();

        this.userRepository.createUser(user);
        return user.getId();
    }

    public UserDTO getUserByID(UUID id) {
        var user = this.userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException());

        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }     

    public void deleteUserByID(UUID id) {
        this.userRepository.deleteUserByID(id);
    }

    public void updateUser(UUID id, String password) {
        var user = this.userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException());

        var passwordEncoded = encoder.encode(password);

        user.setPassword(passwordEncoded);
        this.userRepository.save(user);
    }
}
