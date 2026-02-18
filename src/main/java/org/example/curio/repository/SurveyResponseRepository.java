package org.example.curio.repository;

import org.example.curio.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    List<SurveyResponse> findByFormId(Long formId);

    boolean existsByRecipientId(Long recipientId);
}
