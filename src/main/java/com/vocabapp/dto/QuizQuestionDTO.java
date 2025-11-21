package com.vocabapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDTO {
    private Long vocabularyId;
    private String word;
    private String phonetic; // Pronunciation
    private String type; // "multiple_choice" or "typing"
    private List<String> options; // For multiple choice (4 options)
    private String correctAnswer; // For validation
}
