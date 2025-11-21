package com.vocabapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vocabulary_progress", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"vocabulary_id", "collection_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vocabulary_id", nullable = false)
    @JsonIgnore
    private Vocabulary vocabulary;
    
    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    @JsonIgnore
    private Collection collection;
    
    @Column(nullable = false)
    private Boolean learned = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningStatus learningStatus = LearningStatus.NOT_STARTED;
    
    @Column
    private Boolean firstAttemptCorrect = false;  // Multiple choice test
    
    @Column
    private Boolean secondAttemptCorrect = false; // Typing test
    
    @Column
    private Integer reviewCount = 0;
    
    @Column
    private LocalDateTime lastReviewedAt;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
