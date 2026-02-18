package org.example.curio.dto;

import java.util.List;

public record DistributeFormRequest(
        List<String> emails
) {}
