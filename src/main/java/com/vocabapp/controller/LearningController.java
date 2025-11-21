package com.vocabapp.controller;

import com.vocabapp.dto.QuizAnswerDTO;
import com.vocabapp.dto.QuizQuestionDTO;
import com.vocabapp.dto.QuizResultDTO;
import com.vocabapp.model.User;
import com.vocabapp.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {
    
    private final LearningService learningService;
    
    /**
     * Get next question for a collection
     * GET /api/learning/next/{collectionId}
     */
    @GetMapping("/next/{collectionId}")
    public ResponseEntity<QuizQuestionDTO> getNextQuestion(@PathVariable Long collectionId) {
        QuizQuestionDTO question = learningService.getNextQuestion(collectionId);
        
        if (question == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(question);
    }
    
    /**
     * Submit answer
     * POST /api/learning/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<QuizResultDTO> submitAnswer(
            @RequestBody QuizAnswerDTO answerDTO,
            @AuthenticationPrincipal User user) {
        
        QuizResultDTO result = learningService.submitAnswer(answerDTO, user);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get learning statistics for a collection
     * GET /api/learning/stats/{collectionId}
     */
    @GetMapping("/stats/{collectionId}")
    public ResponseEntity<Map<String, Long>> getLearningStats(@PathVariable Long collectionId) {
        Map<String, Long> stats = learningService.getLearningStats(collectionId);
        return ResponseEntity.ok(stats);
    }
}
