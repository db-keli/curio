package org.example.curio.repository;

import org.example.curio.entity.Form;
import org.example.curio.entity.FormStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormRepository extends JpaRepository<Form, Long> {

    List<Form> findByCreatedById(Long userId);

    List<Form> findByStatus(FormStatus status);
}
