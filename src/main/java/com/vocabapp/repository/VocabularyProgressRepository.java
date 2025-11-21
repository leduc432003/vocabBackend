package com.vocabapp.repository;

import com.vocabapp.model.VocabularyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyProgressRepository extends JpaRepository<VocabularyProgress, Long> {
    
    Optional<VocabularyProgress> findByVocabularyIdAndCollectionId(Long vocabularyId, Long collectionId);
    
    List<VocabularyProgress> findByCollectionId(Long collectionId);
    
    List<VocabularyProgress> findByVocabularyId(Long vocabularyId);
    
    Long countByCollectionIdAndLearned(Long collectionId, Boolean learned);
    
    @Query("SELECT COUNT(vp) FROM VocabularyProgress vp WHERE vp.learned = true")
    Long countAllLearned();
    
    @Query("SELECT vp FROM VocabularyProgress vp WHERE vp.collection.id = :collectionId AND vp.learned = :learned")
    List<VocabularyProgress> findByCollectionIdAndLearned(@Param("collectionId") Long collectionId, @Param("learned") Boolean learned);
}
