package org.example.curio.dto;

import java.util.List;

public record SubmitResponseRequest(
        String token,
        List<AnswerRequest> answers
) {}
