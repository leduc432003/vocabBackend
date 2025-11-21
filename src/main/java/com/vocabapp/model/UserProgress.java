package com.vocabapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
    
    @Column(nullable = false)
    private Integer totalWords = 0;
    
    @Column(nullable = false)
    private Integer learnedWords = 0;
    
    @Column(nullable = false)
    private Integer streakDays = 0;
    
    @Column(nullable = false)
    private Integer studyTimeMinutes = 0;

    @Column(nullable = false)
    private Integer studyTimeToday = 0;

    @Column(nullable = false)
    private Integer wordsLearnedToday = 0;
    
    @Column(nullable = false)
    private Integer quizzesTaken = 0;
    
    @Column(nullable = false)
    private Integer correctAnswers = 0;
    
    @Column(nullable = false)
    private Integer totalAnswers = 0;
    
    @Column
    private LocalDate lastStudyDate;
    
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
