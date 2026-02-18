package org.example.curio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.curio.dto.RegisterRequest;
import org.example.curio.dto.UserDto;
import org.example.curio.entity.User;
import org.example.curio.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        log.info("Registering new user: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }
        User user = User.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);
        log.info("User registered successfully with id={}", user.getId());
        return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getCreatedAt());
    }

    @Transactional
    public User createUser(String email, String name, String rawPassword) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(rawPassword))
                .build();
        return userRepository.save(user);
    }
}
