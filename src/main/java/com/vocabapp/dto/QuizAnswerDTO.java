package com.vocabapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerDTO {
    private Long vocabularyId;
    private Long collectionId;
    private String answer;
    private String testType; // "first" or "second"
}
