package org.example.curio.dto;

import org.example.curio.entity.QuestionType;
import java.util.List;

public record QuestionDto(
        Long id,
        String text,
        QuestionType type,
        Integer position,
        boolean required,
        List<String> options
) {}
