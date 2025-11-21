package com.vocabapp.controller;

import com.vocabapp.model.User;
import com.vocabapp.model.UserProgress;
import com.vocabapp.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "https://vocab-frontend-xi.vercel.app",
        "https://vocab-frontend-delta.vercel.app"
})
public class UserProgressController {
    
    private final UserProgressService userProgressService;
    
    @GetMapping
    public ResponseEntity<UserProgress> getProgress(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProgressService.getOrCreateProgress(user));
    }
    
    @PostMapping("/learned")
    public ResponseEntity<UserProgress> incrementLearnedWords(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProgressService.incrementLearnedWords(user));
    }
    
    @PostMapping("/study-time")
    public ResponseEntity<UserProgress> addStudyTime(@AuthenticationPrincipal User user, @RequestBody Map<String, Integer> request) {
        Integer minutes = request.get("minutes");
        return ResponseEntity.ok(userProgressService.addStudyTime(user, minutes));
    }
    
    @PostMapping("/quiz-result")
    public ResponseEntity<UserProgress> recordQuizResult(@AuthenticationPrincipal User user, @RequestBody Map<String, Integer> request) {
        Integer correct = request.get("correct");
        Integer total = request.get("total");
        return ResponseEntity.ok(userProgressService.recordQuizResult(user, correct, total));
    }
}
