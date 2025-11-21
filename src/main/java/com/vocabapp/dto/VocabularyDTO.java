package com.vocabapp.dto;

import com.vocabapp.model.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyDTO {
    private Long id;
    private String word;
    private String meaning;
    private String phonetic;
    private String wordType;
    private String example;
    private String synonym;
    private String antonym;
    private String category;
    private Integer difficulty;
    private Set<Collection> collections;
    private Boolean learned; // This will be collection-specific
    private Integer reviewCount;
    private LocalDateTime lastReviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
