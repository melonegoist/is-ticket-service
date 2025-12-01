package edu.itmo.isticketservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    private Integer createdCount;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String errorMessage;

}
