package org.example.curio.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.curio.dto.CreateFormRequest;
import org.example.curio.dto.FormDto;
import org.example.curio.entity.User;
import org.example.curio.service.FormService;
import org.example.curio.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
@Slf4j
public class FormController {

    private final FormService formService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<FormDto> createForm(
            @RequestBody CreateFormRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveUser(userDetails);
        log.info("User {} creating form: {}", user.getEmail(), request.title());
        FormDto form = formService.createForm(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(form);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormDto> getForm(@PathVariable Long id) {
        log.debug("Fetching form id={}", id);
        return ResponseEntity.ok(formService.getForm(id));
    }

    @GetMapping
    public ResponseEntity<List<FormDto>> getMyForms(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveUser(userDetails);
        log.debug("Fetching forms for user {}", user.getEmail());
        return ResponseEntity.ok(formService.getFormsByUser(user.getId()));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<FormDto> publishForm(@PathVariable Long id) {
        log.info("Publishing form id={}", id);
        return ResponseEntity.ok(formService.publishForm(id));
    }

    private User resolveUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
