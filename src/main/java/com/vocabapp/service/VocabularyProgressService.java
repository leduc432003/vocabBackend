package com.vocabapp.service;

import com.vocabapp.dto.VocabularyDTO;
import com.vocabapp.model.Collection;
import com.vocabapp.model.Vocabulary;
import com.vocabapp.model.VocabularyProgress;
import com.vocabapp.repository.CollectionRepository;
import com.vocabapp.repository.VocabularyProgressRepository;
import com.vocabapp.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyProgressService {
    
    private final VocabularyProgressRepository progressRepository;
    private final VocabularyRepository vocabularyRepository;
    private final CollectionRepository collectionRepository;
    private final UserProgressService userProgressService;
    
    /**
     * Get or create progress for a vocabulary in a collection
     */
    public VocabularyProgress getOrCreateProgress(Long vocabularyId, Long collectionId) {
        return progressRepository.findByVocabularyIdAndCollectionId(vocabularyId, collectionId)
                .orElseGet(() -> {
                    Vocabulary vocab = vocabularyRepository.findById(vocabularyId)
                            .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
                    Collection collection = collectionRepository.findById(collectionId)
                            .orElseThrow(() -> new RuntimeException("Collection not found"));
                    
                    VocabularyProgress progress = new VocabularyProgress();
                    progress.setVocabulary(vocab);
                    progress.setCollection(collection);
                    progress.setLearned(false);
                    progress.setReviewCount(0);
                    return progressRepository.save(progress);
                });
    }
    
    /**
     * Toggle learned status for a vocabulary in a specific collection
     */
    @Transactional
    public VocabularyProgress toggleLearned(Long vocabularyId, Long collectionId) {
        VocabularyProgress progress = getOrCreateProgress(vocabularyId, collectionId);
        boolean wasUnlearned = !progress.getLearned();
        progress.setLearned(!progress.getLearned());
        progress.setLastReviewedAt(LocalDateTime.now());
        
        VocabularyProgress saved = progressRepository.save(progress);
        
        // Increment wordsLearnedToday if changed to learned
        if (wasUnlearned && saved.getLearned()) {
            userProgressService.incrementWordsLearnedToday(saved.getCollection().getUser());
        }
        
        return saved;
    }
    
    /**
     * Mark as learned in a specific collection
     */
    @Transactional
    public VocabularyProgress markAsLearned(Long vocabularyId, Long collectionId, Boolean learned) {
        VocabularyProgress progress = getOrCreateProgress(vocabularyId, collectionId);
        boolean wasUnlearned = !progress.getLearned();
        progress.setLearned(learned);
        progress.setLastReviewedAt(LocalDateTime.now());
        progress.setReviewCount(progress.getReviewCount() + 1);
        
        VocabularyProgress saved = progressRepository.save(progress);
        
        // Increment wordsLearnedToday if changed to learned
        if (wasUnlearned && learned) {
            userProgressService.incrementWordsLearnedToday(saved.getCollection().getUser());
        }
        
        return saved;
    }
    
    /**
     * Get vocabularies with their learned status for a specific collection
     */
    public List<VocabularyDTO> getVocabulariesWithProgress(Long collectionId) {
        List<Vocabulary> vocabularies = vocabularyRepository.findByCollectionId(collectionId);
        List<VocabularyDTO> result = new ArrayList<>();
        
        for (Vocabulary vocab : vocabularies) {
            VocabularyDTO dto = convertToDTO(vocab);
            
            // Get progress for this collection
            Optional<VocabularyProgress> progress = progressRepository
                    .findByVocabularyIdAndCollectionId(vocab.getId(), collectionId);
            
            if (progress.isPresent()) {
                dto.setLearned(progress.get().getLearned());
                dto.setReviewCount(progress.get().getReviewCount());
                dto.setLastReviewedAt(progress.get().getLastReviewedAt());
            } else {
                dto.setLearned(false);
                dto.setReviewCount(0);
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    /**
     * Get learned count for a collection
     */
    public Long getLearnedCountForCollection(Long collectionId) {
        return progressRepository.countByCollectionIdAndLearned(collectionId, true);
    }
    
    /**
     * Get unlearned count for a collection
     */
    public Long getUnlearnedCountForCollection(Long collectionId) {
        Long total = vocabularyRepository.countByCollectionId(collectionId);
        Long learned = getLearnedCountForCollection(collectionId);
        return total - learned;
    }
    
    /**
     * Convert Vocabulary entity to DTO
     */
    private VocabularyDTO convertToDTO(Vocabulary vocab) {
        VocabularyDTO dto = new VocabularyDTO();
        dto.setId(vocab.getId());
        dto.setWord(vocab.getWord());
        dto.setMeaning(vocab.getMeaning());
        dto.setPhonetic(vocab.getPhonetic());
        dto.setWordType(vocab.getWordType());
        dto.setExample(vocab.getExample());
        dto.setSynonym(vocab.getSynonym());
        dto.setAntonym(vocab.getAntonym());
        dto.setCategory(vocab.getCategory());
        dto.setDifficulty(vocab.getDifficulty());
        dto.setCollections(vocab.getCollections());
        dto.setCreatedAt(vocab.getCreatedAt());
        dto.setUpdatedAt(vocab.getUpdatedAt());
        return dto;
    }
}
