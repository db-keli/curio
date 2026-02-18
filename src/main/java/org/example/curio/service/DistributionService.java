package org.example.curio.service;

import lombok.RequiredArgsConstructor;
import org.example.curio.dto.DistributeFormRequest;
import org.example.curio.entity.*;
import org.example.curio.repository.FormDistributionRepository;
import org.example.curio.repository.FormRepository;
import org.example.curio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DistributionService {

    private final FormRepository formRepository;
    private final FormDistributionRepository distributionRepository;
    private final UserRepository userRepository;

    @Transactional
    public FormDistribution distributeForm(Long formId, DistributeFormRequest request, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FormDistribution distribution = FormDistribution.builder()
                .form(form)
                .sentBy(user)
                .build();

        for (String email : request.emails()) {
            Recipient recipient = Recipient.builder()
                    .distribution(distribution)
                    .email(email)
                    .token(UUID.randomUUID().toString())
                    .status(RecipientStatus.PENDING)
                    .build();
            distribution.getRecipients().add(recipient);
        }

        return distributionRepository.save(distribution);
    }
}
