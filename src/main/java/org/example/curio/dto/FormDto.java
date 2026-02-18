package org.example.curio.dto;

import org.example.curio.entity.FormStatus;
import java.time.LocalDateTime;
import java.util.List;

public record FormDto(
        Long id,
        String title,
        String description,
        FormStatus status,
        Long createdById,
        List<QuestionDto> questions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
