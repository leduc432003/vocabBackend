package com.vocabapp.service;

import com.vocabapp.model.User;
import com.vocabapp.model.UserProgress;
import com.vocabapp.repository.UserProgressRepository;
import com.vocabapp.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserProgressService {
    
    private final UserProgressRepository userProgressRepository;
    private final VocabularyRepository vocabularyRepository;
    
    public UserProgress getOrCreateProgress(User user) {
        UserProgress progress = userProgressRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProgress newProgress = new UserProgress();
                    newProgress.setUser(user);
                    return userProgressRepository.save(newProgress);
                });
        
        // Always sync counts from vocabulary table
        updateTotalWords(progress, user);
        // Check for day changes to reset daily counters and update streak
        updateStreak(progress);
        
        return userProgressRepository.save(progress);
    }
    
    @Transactional
    public UserProgress updateProgress(UserProgress progress, User user) {
        updateTotalWords(progress, user);
        updateStreak(progress);
        return userProgressRepository.save(progress);
    }
    
    @Transactional
    public UserProgress incrementLearnedWords(User user) {
        UserProgress progress = getOrCreateProgress(user);
        progress.setLearnedWords(progress.getLearnedWords() + 1);
        updateStreak(progress);
        return userProgressRepository.save(progress);
    }
    
    @Transactional
    public UserProgress addStudyTime(User user, Integer minutes) {
        UserProgress progress = getOrCreateProgress(user);
        updateStreak(progress);
        progress.setStudyTimeMinutes(progress.getStudyTimeMinutes() + minutes);
        progress.setStudyTimeToday(progress.getStudyTimeToday() + minutes);
        return userProgressRepository.save(progress);
    }
    
    @Transactional
    public UserProgress recordQuizResult(User user, Integer correct, Integer total) {
        UserProgress progress = getOrCreateProgress(user);
        progress.setQuizzesTaken(progress.getQuizzesTaken() + 1);
        progress.setCorrectAnswers(progress.getCorrectAnswers() + correct);
        progress.setTotalAnswers(progress.getTotalAnswers() + total);
        updateStreak(progress);
        return userProgressRepository.save(progress);
    }
    
    @Transactional
    public UserProgress incrementWordsLearnedToday(User user) {
        UserProgress progress = getOrCreateProgress(user);
        progress.setWordsLearnedToday(progress.getWordsLearnedToday() + 1);
        updateStreak(progress);
        return userProgressRepository.save(progress);
    }
    
    private void updateTotalWords(UserProgress progress, User user) {
        Long totalWords = vocabularyRepository.countByUser(user);
        Long learnedWords = vocabularyRepository.countLearnedWordsByUser(user);
        progress.setTotalWords(totalWords.intValue());
        progress.setLearnedWords(learnedWords.intValue());
    }
    
    private void updateStreak(UserProgress progress) {
        LocalDate today = LocalDate.now();
        LocalDate lastStudy = progress.getLastStudyDate();
        
        if (lastStudy == null) {
            progress.setStreakDays(1);
            progress.setLastStudyDate(today);
            progress.setStudyTimeToday(0);
            progress.setWordsLearnedToday(0);
        } else if (lastStudy.equals(today)) {
            // Already studied today, no change
            return;
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastStudy, today);
            if (daysBetween == 1) {
                // Consecutive day
                progress.setStreakDays(progress.getStreakDays() + 1);
            } else if (daysBetween > 1) {
                // Streak broken
                progress.setStreakDays(1);
            }
            progress.setLastStudyDate(today);
            progress.setStudyTimeToday(0);
            progress.setWordsLearnedToday(0);
        }
    }
}
