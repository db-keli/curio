package org.example.curio.dto;

public record AnswerRequest(
        Long questionId,
        String value
) {}
