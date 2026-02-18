package org.example.curio.controller;

import lombok.RequiredArgsConstructor;
import org.example.curio.dto.DistributeFormRequest;
import org.example.curio.service.DistributionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/forms/{formId}/distribute")
@RequiredArgsConstructor
public class DistributionController {

    private final DistributionService distributionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> distribute(
            @PathVariable Long formId,
            @RequestBody DistributeFormRequest request) {
        // TODO: get userId from authenticated principal
        Long userId = 1L;
        distributionService.distributeForm(formId, request, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Form distributed to " + request.emails().size() + " recipients"));
    }
}
