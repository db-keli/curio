package org.example.curio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.curio.dto.*;
import org.example.curio.entity.*;
import org.example.curio.repository.FormRepository;
import org.example.curio.repository.QuestionRepository;
import org.example.curio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormService {

    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public FormDto createForm(CreateFormRequest request, Long userId) {
        log.info("Creating form '{}' for userId={}", request.title(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Form form = Form.builder()
                .title(request.title())
                .description(request.description())
                .createdBy(user)
                .status(FormStatus.DRAFT)
                .build();

        if (request.questions() != null) {
            for (QuestionRequest qr : request.questions()) {
                Question question = Question.builder()
                        .form(form)
                        .text(qr.text())
                        .type(qr.type())
                        .position(qr.position())
                        .required(qr.required())
                        .build();

                if (qr.options() != null) {
                    for (int i = 0; i < qr.options().size(); i++) {
                        QuestionOption option = QuestionOption.builder()
                                .question(question)
                                .text(qr.options().get(i))
                                .position(i)
                                .build();
                        question.getOptions().add(option);
                    }
                }

                form.getQuestions().add(question);
            }
        }

        Form saved = formRepository.save(form);
        log.info("Form created with id={}", saved.getId());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public FormDto getForm(Long formId) {
        log.debug("Fetching form id={}", formId);
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        return toDto(form);
    }

    @Transactional(readOnly = true)
    public List<FormDto> getFormsByUser(Long userId) {
        log.debug("Fetching forms for userId={}", userId);
        return formRepository.findByCreatedById(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public FormDto publishForm(Long formId) {
        log.info("Publishing form id={}", formId);
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        form.setStatus(FormStatus.PUBLISHED);
        return toDto(formRepository.save(form));
    }

    private FormDto toDto(Form form) {
        List<QuestionDto> questions = form.getQuestions().stream()
                .map(q -> new QuestionDto(
                        q.getId(),
                        q.getText(),
                        q.getType(),
                        q.getPosition(),
                        q.isRequired(),
                        q.getOptions().stream().map(QuestionOption::getText).toList()
                ))
                .toList();

        return new FormDto(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getStatus(),
                form.getCreatedBy().getId(),
                questions,
                form.getCreatedAt(),
                form.getUpdatedAt()
        );
    }
}
