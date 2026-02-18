package org.example.curio.repository;

import org.example.curio.entity.Recipient;
import org.example.curio.entity.RecipientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    Optional<Recipient> findByToken(String token);

    List<Recipient> findByDistributionId(Long distributionId);

    List<Recipient> findByStatus(RecipientStatus status);

    List<Recipient> findByDistributionFormIdAndStatus(Long formId, RecipientStatus status);
}
