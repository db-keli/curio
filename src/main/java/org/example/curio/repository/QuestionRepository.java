package org.example.curio.repository;

import org.example.curio.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByFormIdOrderByPositionAsc(Long formId);
}
