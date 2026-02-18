package org.example.curio.service;

import lombok.RequiredArgsConstructor;
import org.example.curio.dto.AnswerRequest;
import org.example.curio.dto.SubmitResponseRequest;
import org.example.curio.entity.*;
import org.example.curio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyResponseService {

    private final SurveyResponseRepository responseRepository;
    private final RecipientRepository recipientRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public SurveyResponse submitResponse(SubmitResponseRequest request) {
        Recipient recipient = recipientRepository.findByToken(request.token())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (recipient.getStatus() == RecipientStatus.COMPLETED) {
            throw new RuntimeException("Survey already completed");
        }

        Form form = recipient.getDistribution().getForm();

        SurveyResponse response = SurveyResponse.builder()
                .recipient(recipient)
                .form(form)
                .build();

        for (AnswerRequest ar : request.answers()) {
            Question question = questionRepository.findById(ar.questionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            Answer answer = Answer.builder()
                    .surveyResponse(response)
                    .question(question)
                    .value(ar.value())
                    .build();
            response.getAnswers().add(answer);
        }

        recipient.setStatus(RecipientStatus.COMPLETED);
        recipient.setCompletedAt(LocalDateTime.now());
        recipientRepository.save(recipient);

        return responseRepository.save(response);
    }

    @Transactional(readOnly = true)
    public List<SurveyResponse> getResponsesByForm(Long formId) {
        return responseRepository.findByFormId(formId);
    }
}
