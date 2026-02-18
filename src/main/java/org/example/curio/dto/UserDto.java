package org.example.curio.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt
) {}
