package org.example.curio.repository;

import org.example.curio.entity.FormDistribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormDistributionRepository extends JpaRepository<FormDistribution, Long> {

    List<FormDistribution> findByFormId(Long formId);
}
