package com.vocabapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private boolean correct;
    private String correctAnswer;
    private String learningStatus; // NOT_STARTED, LEARNING, MASTERED
    private String message;
}
