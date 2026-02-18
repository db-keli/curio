package org.example.curio.repository;

import org.example.curio.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findBySurveyResponseId(Long surveyResponseId);
}
