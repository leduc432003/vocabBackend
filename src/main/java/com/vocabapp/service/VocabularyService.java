package com.vocabapp.service;

import com.vocabapp.model.User;
import com.vocabapp.model.Vocabulary;
import com.vocabapp.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyService {
    private final VocabularyRepository vocabularyRepository;
    private final UserProgressService userProgressService;

    // ========== USER-FILTERED METHODS (NEW) ==========
    
    public List<Vocabulary> getAllVocabulary(User user) {
        return vocabularyRepository.findByUser(user);
    }
    
    public Page<Vocabulary> getAllVocabularyPaged(User user, int page, int size) {
        return vocabularyRepository.findByUser(user, PageRequest.of(page, size, Sort.by("id").descending()));
    }
    
    public Optional<Vocabulary> getVocabularyByWord(String word, User user) {
        return vocabularyRepository.findByWordAndUser(word, user);
    }
    
    public List<Vocabulary> getVocabularyByCategory(String category, User user) {
        return vocabularyRepository.findByCategoryAndUser(category, user);
    }
    
    public List<Vocabulary> searchVocabulary(String keyword, User user) {
        return vocabularyRepository.findByWordContainingIgnoreCaseAndUser(keyword, user);
    }
    
    public List<Vocabulary> getLearnedVocabulary(User user) {
        return vocabularyRepository.findByLearnedAndUser(true, user);
    }
    
    public List<Vocabulary> getUnlearnedVocabulary(User user) {
        return vocabularyRepository.findByLearnedAndUser(false, user);
    }
    
    public Page<Vocabulary> getVocabularyPage(int page, int size, String filter, String keyword, User user) {
        return getVocabularyPage(page, size, filter, keyword, null, user);
    }
    
    public Page<Vocabulary> getVocabularyPage(int page, int size, String filter, String keyword, Long collectionId, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        if (collectionId != null) {
            return vocabularyRepository.findByCollectionIdAndUser(collectionId, user, pageable);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            return vocabularyRepository.findByWordContainingIgnoreCaseAndUser(keyword.trim(), user, pageable);
        }
        
        if ("learned".equalsIgnoreCase(filter)) {
            return vocabularyRepository.findByLearnedAndUser(true, user, pageable);
        } else if ("unlearned".equalsIgnoreCase(filter)) {
            return vocabularyRepository.findByLearnedAndUser(false, user, pageable);
        }
        
        return vocabularyRepository.findByUser(user, pageable);
    }
    
    public List<Vocabulary> getQuizWords(String type, int limit, User user) {
        List<Vocabulary> words;
        if ("learned".equalsIgnoreCase(type)) {
            words = vocabularyRepository.findRandomLearnedWordsByUser(user);
        } else if ("unlearned".equalsIgnoreCase(type)) {
            words = vocabularyRepository.findRandomUnlearnedWordsByUser(user);
        } else {
            words = vocabularyRepository.findRandomWordsByUser(user);
        }
        return words.stream().limit(limit).toList();
    }
    
    public List<Vocabulary> getVocabularyByCollectionId(Long collectionId, User user) {
        return vocabularyRepository.findByCollectionIdAndUser(collectionId, user);
    }
    
    @Transactional
    public Vocabulary createVocabulary(Vocabulary vocabulary, User user) {
        vocabulary.setUser(user);
        return vocabularyRepository.save(vocabulary);
    }
    
    @Transactional
    public Vocabulary updateVocabulary(Long id, Vocabulary vocabularyDetails, User user) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
        
        // Security check: ensure vocabulary belongs to user
        if (!vocabulary.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        vocabulary.setWord(vocabularyDetails.getWord());
        vocabulary.setMeaning(vocabularyDetails.getMeaning());
        vocabulary.setPhonetic(vocabularyDetails.getPhonetic());
        vocabulary.setWordType(vocabularyDetails.getWordType());
        vocabulary.setExample(vocabularyDetails.getExample());
        vocabulary.setSynonym(vocabularyDetails.getSynonym());
        vocabulary.setAntonym(vocabularyDetails.getAntonym());
        vocabulary.setCategory(vocabularyDetails.getCategory());
        vocabulary.setDifficulty(vocabularyDetails.getDifficulty());
        
        return vocabularyRepository.save(vocabulary);
    }
    
    @Transactional
    public void deleteVocabulary(Long id, User user) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
        
        // Security check
        if (!vocabulary.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        vocabularyRepository.delete(vocabulary);
    }
    
    public Long countLearnedWords(User user) {
        return vocabularyRepository.countLearnedWordsByUser(user);
    }
    
    public Long countByCollectionId(Long collectionId, User user) {
        return vocabularyRepository.countByCollectionIdAndUser(collectionId, user);
    }

    // ========== LEGACY METHODS (for backward compatibility) ==========

    public List<Vocabulary> getAllVocabulary() {
        return vocabularyRepository.findAll();
    }

    public Optional<Vocabulary> getVocabularyById(Long id) {
        return vocabularyRepository.findById(id);
    }

    public Optional<Vocabulary> getVocabularyByWord(String word) {
        return vocabularyRepository.findByWord(word);
    }

    public List<Vocabulary> getVocabularyByCategory(String category) {
        return vocabularyRepository.findByCategory(category);
    }

    public List<Vocabulary> searchVocabulary(String keyword) {
        return vocabularyRepository.findByWordContainingIgnoreCase(keyword);
    }

    public List<Vocabulary> getLearnedVocabulary() {
        return vocabularyRepository.findByLearned(true);
    }

    public List<Vocabulary> getUnlearnedVocabulary() {
        return vocabularyRepository.findByLearned(false);
    }

    // Pagination helpers
    public Page<Vocabulary> getAllVocabularyPaged(int page, int size) {
        return vocabularyRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public Page<Vocabulary> getVocabularyByCategoryPaged(String category, int page, int size) {
        return vocabularyRepository.findByCategory(category, PageRequest.of(page, size));
    }

    public Page<Vocabulary> searchVocabularyPaged(String keyword, int page, int size) {
        return vocabularyRepository.findByWordContainingIgnoreCase(keyword, PageRequest.of(page, size));
    }

    public Page<Vocabulary> getLearnedVocabularyPaged(int page, int size) {
        return vocabularyRepository.findByLearned(true, PageRequest.of(page, size));
    }

    public Page<Vocabulary> getUnlearnedVocabularyPaged(int page, int size) {
        return vocabularyRepository.findByLearned(false, PageRequest.of(page, size));
    }

    public Page<Vocabulary> getVocabularyPage(int page, int size, String filter, String keyword) {
        return getVocabularyPage(page, size, filter, keyword, (Long) null);
    }

    public Page<Vocabulary> getVocabularyPage(int page, int size, String filter, String keyword, Long collectionId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (collectionId != null) {
            // If collection filter is provided, ignore other filters for now
            return vocabularyRepository.findByCollectionId(collectionId, pageable);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            return vocabularyRepository.findByWordContainingIgnoreCase(keyword.trim(), pageable);
        }
        if ("learned".equalsIgnoreCase(filter)) {
            return vocabularyRepository.findByLearned(true, pageable);
        } else if ("unlearned".equalsIgnoreCase(filter)) {
            return vocabularyRepository.findByLearned(false, pageable);
        }
        return vocabularyRepository.findAll(pageable);
    }


    // Quiz related queries
    public List<Vocabulary> getRandomWordsForQuiz(int limit) {
        return vocabularyRepository.findRandomWords().stream().limit(limit).toList();
    }

    public List<Vocabulary> getQuizWords(String type, int limit) {
        List<Vocabulary> words;
        if ("learned".equalsIgnoreCase(type)) {
            words = vocabularyRepository.findRandomLearnedWords();
        } else if ("unlearned".equalsIgnoreCase(type)) {
            words = vocabularyRepository.findRandomUnlearnedWords();
        } else {
            words = vocabularyRepository.findRandomWords();
        }
        return words.stream().limit(limit).toList();
    }

    public List<Vocabulary> getWordsForReview(int limit) {
        return vocabularyRepository.findUnlearnedWordsForReview().stream().limit(limit).toList();
    }

    // Collection support
    public List<Vocabulary> getVocabularyByCollectionId(Long collectionId) {
        return vocabularyRepository.findByCollectionId(collectionId);
    }

    // Create / Update / Delete
    @Transactional
    public Vocabulary createVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.save(vocabulary);
    }

    @Transactional
    public Vocabulary updateVocabulary(Long id, Vocabulary vocabularyDetails) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + id));
        
        // Track if learned status changed from false to true
        boolean wasUnlearned = vocabulary.getLearned() == null || !vocabulary.getLearned();
        boolean nowLearned = vocabularyDetails.getLearned() != null && vocabularyDetails.getLearned();
        
        vocabulary.setWord(vocabularyDetails.getWord());
        vocabulary.setMeaning(vocabularyDetails.getMeaning());
        vocabulary.setPhonetic(vocabularyDetails.getPhonetic());
        vocabulary.setWordType(vocabularyDetails.getWordType());
        vocabulary.setExample(vocabularyDetails.getExample());
        vocabulary.setSynonym(vocabularyDetails.getSynonym());
        vocabulary.setAntonym(vocabularyDetails.getAntonym());
        vocabulary.setCategory(vocabularyDetails.getCategory());
        vocabulary.setDifficulty(vocabularyDetails.getDifficulty());
        vocabulary.setLearned(vocabularyDetails.getLearned());
        vocabulary.setReviewCount(vocabularyDetails.getReviewCount());
        vocabulary.setLastReviewedAt(vocabularyDetails.getLastReviewedAt());
        vocabulary.setCollections(vocabularyDetails.getCollections());
        
        Vocabulary saved = vocabularyRepository.save(vocabulary);
        
        // Increment wordsLearnedToday if status changed to learned
        if (wasUnlearned && nowLearned) {
            userProgressService.incrementWordsLearnedToday(vocabulary.getUser());
        }
        
        return saved;
    }

    @Transactional
    public Vocabulary markAsLearned(Long id, Boolean learned) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
        
        boolean wasUnlearned = !vocabulary.getLearned();
        vocabulary.setLearned(learned);
        vocabulary.setLastReviewedAt(LocalDateTime.now());
        if (learned) {
            vocabulary.setReviewCount(vocabulary.getReviewCount() + 1);
        }
        
        Vocabulary saved = vocabularyRepository.save(vocabulary);
        
        if (wasUnlearned && learned) {
            userProgressService.incrementWordsLearnedToday(vocabulary.getUser());
        }
        
        return saved;
    }

    @Transactional
    public void deleteVocabulary(Long id) {
        vocabularyRepository.deleteById(id);
    }

    public Long getTotalCount() {
        return vocabularyRepository.count();
    }

    public Long getLearnedCount() {
        return vocabularyRepository.countLearnedWords();
    }
}
