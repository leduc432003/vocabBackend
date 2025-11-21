package com.vocabapp.repository;

import com.vocabapp.model.User;
import com.vocabapp.model.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    
    // User-filtered queries
    List<Vocabulary> findByUser(User user);
    Page<Vocabulary> findByUser(User user, Pageable pageable);
    
    Optional<Vocabulary> findByWordAndUser(String word, User user);
    
    List<Vocabulary> findByCategoryAndUser(String category, User user);
    Page<Vocabulary> findByCategoryAndUser(String category, User user, Pageable pageable);
    
    List<Vocabulary> findByLearnedAndUser(Boolean learned, User user);
    Page<Vocabulary> findByLearnedAndUser(Boolean learned, User user, Pageable pageable);
    
    List<Vocabulary> findByWordContainingIgnoreCaseAndUser(String word, User user);
    Page<Vocabulary> findByWordContainingIgnoreCaseAndUser(String word, User user, Pageable pageable);
    
    @Query("SELECT v FROM Vocabulary v JOIN v.collections c WHERE c.id = :collectionId AND v.user = :user")
    List<Vocabulary> findByCollectionIdAndUser(Long collectionId, User user);
    
    @Query("SELECT v FROM Vocabulary v JOIN v.collections c WHERE c.id = :collectionId AND v.user = :user")
    Page<Vocabulary> findByCollectionIdAndUser(Long collectionId, User user, Pageable pageable);
    
    @Query("SELECT v FROM Vocabulary v WHERE v.learned = false AND v.user = :user ORDER BY v.reviewCount ASC, RAND()")
    List<Vocabulary> findUnlearnedWordsForReviewByUser(User user);
    
    @Query("SELECT v FROM Vocabulary v WHERE v.user = :user ORDER BY RAND()")
    List<Vocabulary> findRandomWordsByUser(User user);

    @Query("SELECT v FROM Vocabulary v WHERE v.learned = true AND v.user = :user ORDER BY RAND()")
    List<Vocabulary> findRandomLearnedWordsByUser(User user);

    @Query("SELECT v FROM Vocabulary v WHERE v.learned = false AND v.user = :user ORDER BY RAND()")
    List<Vocabulary> findRandomUnlearnedWordsByUser(User user);
    
    @Query("SELECT COUNT(v) FROM Vocabulary v WHERE v.learned = true AND v.user = :user")
    Long countLearnedWordsByUser(User user);
    
    Long countByUser(User user);
    
    @Query("SELECT COUNT(v) FROM Vocabulary v JOIN v.collections c WHERE c.id = :collectionId AND v.user = :user")
    Long countByCollectionIdAndUser(Long collectionId, User user);
    
    // Legacy methods (keep for backward compatibility during migration)
    Optional<Vocabulary> findByWord(String word);
    List<Vocabulary> findByCategory(String category);
    Page<Vocabulary> findByCategory(String category, Pageable pageable);
    List<Vocabulary> findByLearned(Boolean learned);
    Page<Vocabulary> findByLearned(Boolean learned, Pageable pageable);
    
    @Query("SELECT v FROM Vocabulary v JOIN v.collections c WHERE c.id = :collectionId")
    List<Vocabulary> findByCollectionId(Long collectionId);
    
    @Query("SELECT v FROM Vocabulary v JOIN v.collections c WHERE c.id = :collectionId")
    Page<Vocabulary> findByCollectionId(Long collectionId, Pageable pageable);
    
    List<Vocabulary> findByDifficulty(Integer difficulty);
    List<Vocabulary> findByWordContainingIgnoreCase(String word);
    Page<Vocabulary> findByWordContainingIgnoreCase(String word, Pageable pageable);
    
    @Query("SELECT v FROM Vocabulary v WHERE v.learned = false ORDER BY v.reviewCount ASC, RAND()")
    List<Vocabulary> findUnlearnedWordsForReview();
    
    @Query("SELECT v FROM Vocabulary v ORDER BY RAND()")
    List<Vocabulary> findRandomWords();

    @Query("SELECT v FROM Vocabulary v WHERE v.learned = true ORDER BY RAND()")
    List<Vocabulary> findRandomLearnedWords();

    @Query("SELECT v FROM Vocabulary v WHERE v.learned = false ORDER BY RAND()")
    List<Vocabulary> findRandomUnlearnedWords();
    
    @Query("SELECT COUNT(v) FROM Vocabulary v WHERE v.learned = true")
    Long countLearnedWords();
    
    @Query("SELECT COUNT(v) FROM Vocabulary v JOIN v.collections c WHERE c.id = :collectionId")
    Long countByCollectionId(Long collectionId);
}
