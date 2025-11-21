package com.vocabapp.service;

import com.vocabapp.model.Collection;
import com.vocabapp.model.User;
import com.vocabapp.model.Vocabulary;
import com.vocabapp.repository.CollectionRepository;
import com.vocabapp.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {
    
    private final CollectionRepository collectionRepository;
    private final VocabularyRepository vocabularyRepository;
    
    // Get all collections for a user
    public List<Collection> getAllCollections(User user) {
        return collectionRepository.findByUser(user);
    }
    
    public Page<Collection> getAllCollectionsPaged(User user, Pageable pageable) {
        return collectionRepository.findByUser(user, pageable);
    }
    
    // Get collection by ID (user-specific)
    public Optional<Collection> getCollectionById(Long id, User user) {
        return collectionRepository.findByIdAndUser(id, user);
    }
    
    // Create collection
    @Transactional
    public Collection createCollection(Collection collection, User user) {
        collection.setUser(user);
        return collectionRepository.save(collection);
    }
    
    // Update collection
    @Transactional
    public Collection updateCollection(Long id, Collection collectionDetails, User user) {
        Collection collection = collectionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Collection not found or access denied"));
        
        collection.setName(collectionDetails.getName());
        collection.setDescription(collectionDetails.getDescription());
        if (collectionDetails.getIsPublic() != null) {
            collection.setIsPublic(collectionDetails.getIsPublic());
        }
        
        return collectionRepository.save(collection);
    }
    
    // Delete collection
    // Delete collection
    @Transactional
    public void deleteCollection(Long id, User user) {
        Collection collection = collectionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Collection not found or access denied"));
        
        // Remove collection from all vocabularies to avoid foreign key constraint violation
        List<Vocabulary> vocabularies = vocabularyRepository.findByCollectionId(id);
        for (Vocabulary vocab : vocabularies) {
            vocab.getCollections().remove(collection);
            vocabularyRepository.save(vocab);
        }
        
        collectionRepository.delete(collection);
    }
    
    // Toggle collection visibility
    @Transactional
    public Collection toggleVisibility(Long id, User user) {
        Collection collection = collectionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Collection not found or access denied"));
        collection.setIsPublic(!collection.getIsPublic());
        return collectionRepository.save(collection);
    }
    
    // Public collection search
    public Page<Collection> searchPublicCollections(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return collectionRepository.findByIsPublicTrue(pageable);
        }
        return collectionRepository.searchPublicCollections(keyword, pageable);
    }
    
    // Copy public collection to user's library
    @Transactional
    public Collection copyPublicCollection(Long sourceCollectionId, User targetUser) {
        Collection sourceCollection = collectionRepository.findById(sourceCollectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        
        if (!sourceCollection.getIsPublic()) {
            throw new RuntimeException("Collection is not public");
        }
        
        // Create new collection
        Collection newCollection = new Collection();
        newCollection.setName(sourceCollection.getName() + " (Copy)");
        newCollection.setDescription(sourceCollection.getDescription());
        newCollection.setUser(targetUser);
        newCollection.setIsPublic(false);
        
        Collection savedCollection = collectionRepository.save(newCollection);
        
        // Copy all vocabularies from source collection
        List<Vocabulary> sourceVocabs = vocabularyRepository.findByCollectionId(sourceCollectionId);
        for (Vocabulary sourceVocab : sourceVocabs) {
            // Check if user already has this word
            Optional<Vocabulary> existingVocab = vocabularyRepository.findByWordAndUser(sourceVocab.getWord(), targetUser);
            
            if (existingVocab.isPresent()) {
                // Reuse existing vocabulary
                Vocabulary vocab = existingVocab.get();
                vocab.getCollections().add(savedCollection);
                vocabularyRepository.save(vocab);
            } else {
                // Create new vocabulary
                Vocabulary newVocab = new Vocabulary();
                newVocab.setWord(sourceVocab.getWord());
                newVocab.setMeaning(sourceVocab.getMeaning());
                newVocab.setPhonetic(sourceVocab.getPhonetic());
                newVocab.setWordType(sourceVocab.getWordType());
                newVocab.setExample(sourceVocab.getExample());
                newVocab.setSynonym(sourceVocab.getSynonym());
                newVocab.setAntonym(sourceVocab.getAntonym());
                newVocab.setCategory(sourceVocab.getCategory());
                newVocab.setDifficulty(sourceVocab.getDifficulty());
                newVocab.setUser(targetUser);
                newVocab.setLearned(false); // Reset learned status
                newVocab.setReviewCount(0);
                newVocab.getCollections().add(savedCollection);
                
                vocabularyRepository.save(newVocab);
            }
        }
        
        return savedCollection;
    }

    // Get vocabularies of a collection (if public or owned by user)
    public List<Vocabulary> getCollectionVocabularies(Long collectionId, User user) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        
        if (!collection.getIsPublic() && !collection.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: Collection is private and does not belong to you");
        }
        
        return vocabularyRepository.findByCollectionId(collectionId);
    }
    
    // Legacy methods (for backward compatibility)
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }
    
    public Optional<Collection> getCollectionById(Long id) {
        return collectionRepository.findById(id);
    }
    
    public Collection createCollection(Collection collection) {
        return collectionRepository.save(collection);
    }
    
    public Collection updateCollection(Long id, Collection collectionDetails) {
        return collectionRepository.findById(id)
                .map(collection -> {
                    collection.setName(collectionDetails.getName());
                    collection.setDescription(collectionDetails.getDescription());
                    return collectionRepository.save(collection);
                })
                .orElseThrow(() -> new RuntimeException("Collection not found with id " + id));
    }
    
    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }
}
