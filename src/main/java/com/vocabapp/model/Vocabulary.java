package com.vocabapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vocabulary", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"word", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Word is required")
    @Column(nullable = false)
    private String word;
    
    @NotBlank(message = "Meaning is required")
    @Column(nullable = false, length = 500)
    private String meaning;
    
    @Column(length = 100)
    private String phonetic;
    
    @Column(length = 50)
    private String wordType; // noun, verb, adjective, etc.
    
    @Column(length = 1000)
    private String example;
    
    @Column(length = 500)
    private String synonym;
    
    @Column(length = 500)
    private String antonym;
    
    @Column(length = 100)
    private String category; // Basic, Intermediate, Advanced, TOEIC, IELTS, etc.
    
    @Column(nullable = false)
    private Integer difficulty = 1; // 1-5 scale
    
    @ManyToMany
    @JoinTable(
        name = "vocabulary_collection",
        joinColumns = @JoinColumn(name = "vocabulary_id"),
        inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    private Set<Collection> collections = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Column(nullable = false)
    private Boolean learned = false;
    
    @Column(nullable = false)
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
