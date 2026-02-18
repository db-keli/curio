package org.example.curio.controller;

import lombok.RequiredArgsConstructor;
import org.example.curio.dto.SubmitResponseRequest;
import org.example.curio.service.SurveyResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SurveyResponseController {

    private final SurveyResponseService responseService;

    @PostMapping("/responses")
    public ResponseEntity<Map<String, String>> submitResponse(@RequestBody SubmitResponseRequest request) {
        responseService.submitResponse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Response submitted successfully"));
    }

    @GetMapping("/forms/{formId}/responses")
    public ResponseEntity<?> getResponses(@PathVariable Long formId) {
        return ResponseEntity.ok(responseService.getResponsesByForm(formId));
    }
}
