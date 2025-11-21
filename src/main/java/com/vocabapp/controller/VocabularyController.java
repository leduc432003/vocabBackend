package com.vocabapp.controller;

import com.vocabapp.dto.VocabularyDTO;
import com.vocabapp.model.Collection;
import com.vocabapp.model.User;
import com.vocabapp.model.Vocabulary;
import com.vocabapp.model.VocabularyProgress;
import com.vocabapp.repository.CollectionRepository;
import com.vocabapp.service.VocabularyProgressService;
import com.vocabapp.service.VocabularyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocabulary")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "https://vocab-frontend-xi.vercel.app",
        "https://vocab-frontend-delta.vercel.app"
})
public class VocabularyController {
    
    private final VocabularyService vocabularyService;
    private final VocabularyProgressService progressService;
    private final CollectionRepository collectionRepository;
    
    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabulary(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(vocabularyService.getAllVocabulary(user));
    }
    
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Vocabulary> getVocabularyById(@PathVariable Long id) {
        return vocabularyService.getVocabularyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/word/{word}")
    public ResponseEntity<Vocabulary> getVocabularyByWord(@PathVariable String word) {
        return vocabularyService.getVocabularyByWord(word)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Vocabulary>> getVocabularyByCategory(@PathVariable String category) {
        return ResponseEntity.ok(vocabularyService.getVocabularyByCategory(category));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Vocabulary>> searchVocabulary(@RequestParam String keyword) {
        return ResponseEntity.ok(vocabularyService.searchVocabulary(keyword));
    }
    
    @GetMapping("/learned")
    public ResponseEntity<List<Vocabulary>> getLearnedVocabulary() {
        return ResponseEntity.ok(vocabularyService.getLearnedVocabulary());
    }
    
    @GetMapping("/unlearned")
    public ResponseEntity<List<Vocabulary>> getUnlearnedVocabulary() {
        return ResponseEntity.ok(vocabularyService.getUnlearnedVocabulary());
    }
    
    @GetMapping("/random")
    public ResponseEntity<List<Vocabulary>> getRandomVocabulary(@RequestParam(defaultValue = "10") Integer count) {
        return ResponseEntity.ok(vocabularyService.getRandomWordsForQuiz(count));
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<Vocabulary>> getVocabularyPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vocabularyService.getVocabularyPage(page, size, "all", ""));
    }
    
    @GetMapping("/list")
    public ResponseEntity<Page<Vocabulary>> getVocabularyList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long collectionId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(vocabularyService.getVocabularyPage(page, size, filter, keyword, collectionId, user));
    }
    
    @GetMapping("/quiz")
    public ResponseEntity<Map<String, Object>> getQuizWords(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "unlearned") String type,
            @AuthenticationPrincipal User user) {
        List<Vocabulary> words = vocabularyService.getQuizWords(type, count, user);
        Map<String, Object> result = new HashMap<>();
        result.put("data", words);
        result.put("count", words.size());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/collection/{id}")
    public ResponseEntity<Map<String, Object>> getVocabularyByCollectionId(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        List<Vocabulary> vocabularies = vocabularyService.getVocabularyByCollectionId(id, user);
        Map<String, Object> result = new HashMap<>();
        result.put("data", vocabularies);
        result.put("count", vocabularies.size());
        return ResponseEntity.ok(result);
    }
    
    // NEW: Get vocabularies with collection-specific progress
    @GetMapping("/collection/{id}/progress")
    public ResponseEntity<Map<String, Object>> getVocabularyWithProgress(@PathVariable Long id) {
        List<VocabularyDTO> vocabularies = progressService.getVocabulariesWithProgress(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", vocabularies);
        result.put("count", vocabularies.size());
        result.put("learned", progressService.getLearnedCountForCollection(id));
        result.put("unlearned", progressService.getUnlearnedCountForCollection(id));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Vocabulary> createVocabulary(
            @Valid @RequestBody Vocabulary vocabulary,
            @AuthenticationPrincipal User user) {
        Vocabulary created = vocabularyService.createVocabulary(vocabulary, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Vocabulary> updateVocabulary(
            @PathVariable Long id,
            @Valid @RequestBody Vocabulary vocabulary,
            @AuthenticationPrincipal User user) {
        try {
            Vocabulary updated = vocabularyService.updateVocabulary(id, vocabulary, user);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/learned")
    public ResponseEntity<Vocabulary> markAsLearned(
            @PathVariable Long id,
            @RequestParam Boolean learned) {
        try {
            Vocabulary updated = vocabularyService.markAsLearned(id, learned);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // NEW: Toggle learned status for a vocabulary in a specific collection
    @PatchMapping("/{vocabularyId}/collection/{collectionId}/toggle-learned")
    public ResponseEntity<VocabularyProgress> toggleLearnedInCollection(
            @PathVariable Long vocabularyId,
            @PathVariable Long collectionId) {
        try {
            VocabularyProgress progress = progressService.toggleLearned(vocabularyId, collectionId);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // NEW: Mark vocabulary as learned/unlearned in a specific collection
    @PatchMapping("/{vocabularyId}/collection/{collectionId}/learned")
    public ResponseEntity<VocabularyProgress> markAsLearnedInCollection(
            @PathVariable Long vocabularyId,
            @PathVariable Long collectionId,
            @RequestParam Boolean learned) {
        try {
            VocabularyProgress progress = progressService.markAsLearned(vocabularyId, collectionId, learned);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVocabulary(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            vocabularyService.deleteVocabulary(id, user);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Add vocabulary to a collection
    @PostMapping("/{vocabularyId}/collection/{collectionId}")
    public ResponseEntity<Vocabulary> addToCollection(
            @PathVariable Long vocabularyId,
            @PathVariable Long collectionId,
            @AuthenticationPrincipal User user) {
        try {
            Vocabulary vocabulary = vocabularyService.getVocabularyById(vocabularyId)
                    .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
            
            // Security check
            if (!vocabulary.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Collection collection = collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new RuntimeException("Collection not found"));
            
            vocabulary.getCollections().add(collection);
            Vocabulary updated = vocabularyService.createVocabulary(vocabulary, user);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Remove vocabulary from a collection
    @DeleteMapping("/{vocabularyId}/collection/{collectionId}")
    public ResponseEntity<Vocabulary> removeFromCollection(
            @PathVariable Long vocabularyId,
            @PathVariable Long collectionId) {
        try {
            Vocabulary vocabulary = vocabularyService.getVocabularyById(vocabularyId)
                    .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
            Collection collection = collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new RuntimeException("Collection not found"));
            
            vocabulary.getCollections().remove(collection);
            Vocabulary updated = vocabularyService.createVocabulary(vocabulary);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
