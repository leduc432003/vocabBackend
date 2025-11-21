package com.vocabapp.controller;

import com.vocabapp.model.Collection;
import com.vocabapp.model.User;
import com.vocabapp.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "https://vocab-frontend-xi.vercel.app"
})
public class CollectionController {
    
    private final CollectionService collectionService;
    
    @GetMapping
    public ResponseEntity<List<Collection>> getAllCollections(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(collectionService.getAllCollections(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return collectionService.getCollectionById(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Collection> createCollection(
            @Valid @RequestBody Collection collection,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectionService.createCollection(collection, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(
            @PathVariable Long id,
            @Valid @RequestBody Collection collection,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(collectionService.updateCollection(id, collection, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            collectionService.deleteCollection(id, user);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Toggle collection visibility (public/private)
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<Collection> toggleVisibility(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(collectionService.toggleVisibility(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Search public collections
    @GetMapping("/public")
    public ResponseEntity<Page<Collection>> searchPublicCollections(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(collectionService.searchPublicCollections(keyword, pageable));
    }
    
    // Copy public collection to user's library
    @PostMapping("/{id}/copy")
    public ResponseEntity<?> copyPublicCollection(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            Collection copiedCollection = collectionService.copyPublicCollection(id, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(copiedCollection);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get vocabularies of a collection
    @GetMapping("/{id}/vocabularies")
    public ResponseEntity<?> getCollectionVocabularies(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(collectionService.getCollectionVocabularies(id, user));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }
}
