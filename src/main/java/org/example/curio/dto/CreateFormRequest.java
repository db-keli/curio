package org.example.curio.dto;

import java.util.List;

public record CreateFormRequest(
        String title,
        String description,
        List<QuestionRequest> questions
) {}
