package com.vocabapp.repository;

import com.vocabapp.model.Collection;
import com.vocabapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
    // User-filtered queries
    List<Collection> findByUser(User user);
    Page<Collection> findByUser(User user, Pageable pageable);
    
    Optional<Collection> findByIdAndUser(Long id, User user);
    
    // Public collection queries
    List<Collection> findByIsPublicTrue();
    Page<Collection> findByIsPublicTrue(Pageable pageable);
    
    @Query("SELECT c FROM Collection c WHERE c.isPublic = true AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Collection> searchPublicCollections(String keyword, Pageable pageable);
}
