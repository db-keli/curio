package org.example.curio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "form_distributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by", nullable = false)
    private User sentBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @OneToMany(mappedBy = "distribution", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Recipient> recipients = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}
