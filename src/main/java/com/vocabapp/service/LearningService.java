package com.vocabapp.service;

import com.vocabapp.dto.QuizAnswerDTO;
import com.vocabapp.dto.QuizQuestionDTO;
import com.vocabapp.dto.QuizResultDTO;
import com.vocabapp.model.LearningStatus;
import com.vocabapp.model.User;
import com.vocabapp.model.Vocabulary;
import com.vocabapp.model.VocabularyProgress;
import com.vocabapp.repository.CollectionRepository;
import com.vocabapp.repository.VocabularyProgressRepository;
import com.vocabapp.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {
    
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyProgressRepository progressRepository;
    private final CollectionRepository collectionRepository;
    
    /**
     * Get next word to learn based on learning status
     * Priority: NOT_STARTED -> LEARNING
     */
    public QuizQuestionDTO getNextQuestion(Long collectionId) {
        com.vocabapp.model.Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        
        // Auto-initialize progress for all vocabularies in collection if not exists
        List<Vocabulary> allVocabs = vocabularyRepository.findByCollectionId(collectionId);
        for (Vocabulary vocab : allVocabs) {
            if (!progressRepository.findByVocabularyIdAndCollectionId(vocab.getId(), collectionId).isPresent()) {
                VocabularyProgress newProgress = new VocabularyProgress();
                newProgress.setVocabulary(vocab);
                newProgress.setCollection(collection);
                newProgress.setLearningStatus(LearningStatus.NOT_STARTED);
                newProgress.setLearned(false);
                newProgress.setReviewCount(0);
                progressRepository.save(newProgress);
            }
        }
        
        // First, try to get NOT_STARTED words
        List<VocabularyProgress> notStarted = progressRepository
                .findByCollectionIdAndLearningStatus(collectionId, LearningStatus.NOT_STARTED);
        
        if (!notStarted.isEmpty()) {
            VocabularyProgress progress = notStarted.get(0);
            return createMultipleChoiceQuestion(progress.getVocabulary(), collectionId);
        }
        
        // Then, try to get LEARNING words
        List<VocabularyProgress> learning = progressRepository
                .findByCollectionIdAndLearningStatus(collectionId, LearningStatus.LEARNING);
        
        if (!learning.isEmpty()) {
            VocabularyProgress progress = learning.get(0);
            return createTypingQuestion(progress.getVocabulary());
        }
        
        // No words to learn
        return null;
    }
    
    /**
     * Create multiple choice question (4 options)
     */
    private QuizQuestionDTO createMultipleChoiceQuestion(Vocabulary vocabulary, Long collectionId) {
        QuizQuestionDTO question = new QuizQuestionDTO();
        question.setVocabularyId(vocabulary.getId());
        question.setWord(vocabulary.getWord());
        question.setPhonetic(vocabulary.getPhonetic());
        question.setType("multiple_choice");
        
        // Get 3 random wrong answers from the same collection
        List<Vocabulary> allWords = vocabularyRepository.findByCollectionId(collectionId);
        List<String> wrongAnswers = allWords.stream()
                .filter(v -> !v.getId().equals(vocabulary.getId()))
                .map(Vocabulary::getMeaning)
                .collect(Collectors.toList());
        
        Collections.shuffle(wrongAnswers);
        List<String> options = new ArrayList<>();
        options.add(vocabulary.getMeaning()); // Correct answer
        
        // Add 3 wrong answers
        for (int i = 0; i < Math.min(3, wrongAnswers.size()); i++) {
            options.add(wrongAnswers.get(i));
        }
        
        // If not enough words in collection, add dummy options
        while (options.size() < 4) {
            options.add("Option " + (options.size() + 1));
        }
        
        Collections.shuffle(options);
        question.setOptions(options);
        question.setCorrectAnswer(vocabulary.getMeaning());
        
        return question;
    }
    
    /**
     * Create typing question
     */
    private QuizQuestionDTO createTypingQuestion(Vocabulary vocabulary) {
        QuizQuestionDTO question = new QuizQuestionDTO();
        question.setVocabularyId(vocabulary.getId());
        question.setWord(vocabulary.getMeaning()); // Show meaning, ask for word
        question.setPhonetic(vocabulary.getPhonetic());
        question.setType("typing");
        question.setCorrectAnswer(vocabulary.getWord());
        
        return question;
    }
    
    /**
     * Submit answer and update progress
     */
    @Transactional
    public QuizResultDTO submitAnswer(QuizAnswerDTO answerDTO, User user) {
        Vocabulary vocabulary = vocabularyRepository.findById(answerDTO.getVocabularyId())
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));
        
        com.vocabapp.model.Collection collection = collectionRepository.findById(answerDTO.getCollectionId())
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        
        // Get or create progress
        VocabularyProgress progress = progressRepository
                .findByVocabularyIdAndCollectionId(answerDTO.getVocabularyId(), answerDTO.getCollectionId())
                .orElseGet(() -> {
                    VocabularyProgress newProgress = new VocabularyProgress();
                    newProgress.setVocabulary(vocabulary);
                    newProgress.setCollection(collection);
                    newProgress.setLearningStatus(LearningStatus.NOT_STARTED);
                    return newProgress;
                });
        
        QuizResultDTO result = new QuizResultDTO();
        
        if ("first".equals(answerDTO.getTestType())) {
            // First test: Multiple choice
            boolean correct = answerDTO.getAnswer().trim().equalsIgnoreCase(vocabulary.getMeaning().trim());
            result.setCorrect(correct);
            result.setCorrectAnswer(vocabulary.getMeaning());
            
            if (correct) {
                progress.setLearningStatus(LearningStatus.LEARNING);
                progress.setFirstAttemptCorrect(true);
                result.setMessage("Correct! Now let's practice typing this word.");
                result.setLearningStatus("LEARNING");
            } else {
                progress.setLearningStatus(LearningStatus.NOT_STARTED);
                progress.setFirstAttemptCorrect(false);
                result.setMessage("Incorrect. Try again!");
                result.setLearningStatus("NOT_STARTED");
            }
        } else {
            // Second test: Typing
            boolean correct = answerDTO.getAnswer().trim().equalsIgnoreCase(vocabulary.getWord().trim());
            result.setCorrect(correct);
            result.setCorrectAnswer(vocabulary.getWord());
            
            if (correct) {
                progress.setLearningStatus(LearningStatus.MASTERED);
                progress.setSecondAttemptCorrect(true);
                progress.setLearned(true);
                result.setMessage("Excellent! You've mastered this word!");
                result.setLearningStatus("MASTERED");
            } else {
                progress.setLearningStatus(LearningStatus.LEARNING);
                progress.setSecondAttemptCorrect(false);
                result.setMessage("Not quite right. Keep practicing!");
                result.setLearningStatus("LEARNING");
            }
        }
        
        progress.setReviewCount(progress.getReviewCount() + 1);
        progress.setLastReviewedAt(LocalDateTime.now());
        progressRepository.save(progress);
        
        return result;
    }
    
    /**
     * Get learning statistics for a collection
     */
    public Map<String, Long> getLearningStats(Long collectionId) {
        Map<String, Long> stats = new HashMap<>();
        
        long notStarted = progressRepository
                .findByCollectionIdAndLearningStatus(collectionId, LearningStatus.NOT_STARTED)
                .size();
        
        long learning = progressRepository
                .findByCollectionIdAndLearningStatus(collectionId, LearningStatus.LEARNING)
                .size();
        
        long mastered = progressRepository
                .findByCollectionIdAndLearningStatus(collectionId, LearningStatus.MASTERED)
                .size();
        
        stats.put("notStarted", notStarted);
        stats.put("learning", learning);
        stats.put("mastered", mastered);
        stats.put("total", notStarted + learning + mastered);
        
        return stats;
    }
}
