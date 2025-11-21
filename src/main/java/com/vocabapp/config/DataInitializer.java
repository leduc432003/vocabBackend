package com.vocabapp.config;

import com.vocabapp.model.Collection;
import com.vocabapp.model.User;
import com.vocabapp.model.Vocabulary;
import com.vocabapp.repository.CollectionRepository;
import com.vocabapp.repository.UserRepository;
import com.vocabapp.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create default user if not exists
        User adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("admin");
                    user.setEmail("admin@example.com");
                    user.setPassword(passwordEncoder.encode("password123"));
                    return userRepository.save(user);
                });

        if (vocabularyRepository.count() == 0) {
            initializeSampleData(adminUser);
        }
    }
    
    private void initializeSampleData(User user) {
        List<Vocabulary> sampleWords = Arrays.asList(
            createVocab("Hello", "Xin chào", "/həˈloʊ/", "interjection", 
                "Hello, how are you?", "Hi, Greetings", "Goodbye", "Basic", 1, user),
            
            createVocab("Beautiful", "Đẹp", "/ˈbjuːtɪfl/", "adjective", 
                "She is a beautiful woman.", "Pretty, Gorgeous", "Ugly", "Basic", 1, user),
            
            createVocab("Important", "Quan trọng", "/ɪmˈpɔːrtnt/", "adjective", 
                "This is an important meeting.", "Significant, Crucial", "Unimportant", "Intermediate", 2, user),
            
            createVocab("Achieve", "Đạt được", "/əˈtʃiːv/", "verb", 
                "She achieved her goal.", "Accomplish, Attain", "Fail", "Intermediate", 2, user),
            
            createVocab("Knowledge", "Kiến thức", "/ˈnɑːlɪdʒ/", "noun", 
                "He has extensive knowledge of history.", "Information, Understanding", "Ignorance", "Intermediate", 2, user),
            
            createVocab("Magnificent", "Tuyệt vời, lộng lẫy", "/mæɡˈnɪfɪsnt/", "adjective", 
                "The view from the mountain was magnificent.", "Splendid, Superb", "Ordinary", "Advanced", 3, user),
            
            createVocab("Perseverance", "Sự kiên trì", "/ˌpɜːrsəˈvɪrəns/", "noun", 
                "Success requires perseverance.", "Persistence, Determination", "Laziness", "Advanced", 4, user),
            
            createVocab("Eloquent", "Hùng biện, lưu loát", "/ˈeləkwənt/", "adjective", 
                "She gave an eloquent speech.", "Articulate, Fluent", "Inarticulate", "Advanced", 4, user),
            
            createVocab("Implement", "Thực hiện", "/ˈɪmplɪment/", "verb", 
                "We need to implement the new policy.", "Execute, Apply", "Ignore", "TOEIC", 3, user),
            
            createVocab("Collaborate", "Hợp tác", "/kəˈlæbəreɪt/", "verb", 
                "We collaborate with international partners.", "Cooperate, Work together", "Compete", "TOEIC", 2, user),
            
            createVocab("Analyze", "Phân tích", "/ˈænəlaɪz/", "verb", 
                "We need to analyze the data.", "Examine, Study", "Ignore", "IELTS", 3, user),
            
            createVocab("Significant", "Đáng kể", "/sɪɡˈnɪfɪkənt/", "adjective", 
                "There was a significant improvement.", "Important, Notable", "Insignificant", "IELTS", 3, user),
            
            createVocab("Demonstrate", "Chứng minh, thể hiện", "/ˈdemənstreɪt/", "verb", 
                "He demonstrated his skills.", "Show, Prove", "Hide", "IELTS", 3, user),
            
            createVocab("Comprehensive", "Toàn diện", "/ˌkɑːmprɪˈhensɪv/", "adjective", 
                "We need a comprehensive solution.", "Complete, Thorough", "Incomplete", "Advanced", 4, user),
            
            createVocab("Enthusiastic", "Nhiệt tình", "/ɪnˌθuːziˈæstɪk/", "adjective", 
                "She is enthusiastic about the project.", "Eager, Passionate", "Apathetic", "Intermediate", 2, user),
            
            createVocab("Opportunity", "Cơ hội", "/ˌɑːpərˈtuːnəti/", "noun", 
                "This is a great opportunity.", "Chance, Possibility", "Obstacle", "Intermediate", 2, user),
            
            createVocab("Challenge", "Thử thách", "/ˈtʃælɪndʒ/", "noun", 
                "We face many challenges.", "Difficulty, Problem", "Ease", "Intermediate", 2, user),
            
            createVocab("Efficient", "Hiệu quả", "/ɪˈfɪʃnt/", "adjective", 
                "This is an efficient method.", "Effective, Productive", "Inefficient", "TOEIC", 3, user),
            
            createVocab("Innovation", "Sự đổi mới", "/ˌɪnəˈveɪʃn/", "noun", 
                "Innovation drives progress.", "Invention, Creativity", "Tradition", "Advanced", 3, user),
            
            createVocab("Resilient", "Kiên cường, bền bỉ", "/rɪˈzɪliənt/", "adjective", 
                "She is resilient in difficult times.", "Strong, Tough", "Weak", "Advanced", 4, user)
        );
        
        vocabularyRepository.saveAll(sampleWords);
        
        // Create a sample public collection
        if (collectionRepository.count() == 0) {
            Collection publicCollection = new Collection();
            publicCollection.setName("3000 Common English Words");
            publicCollection.setDescription("A collection of the most frequently used English words.");
            publicCollection.setUser(user);
            publicCollection.setIsPublic(true);
            
            collectionRepository.save(publicCollection);
            
            // Add vocabularies to this collection
            for (Vocabulary vocab : sampleWords) {
                vocab.getCollections().add(publicCollection);
                vocabularyRepository.save(vocab);
            }
            
            System.out.println("Sample public collection created.");
        }
    }
    
    private Vocabulary createVocab(String word, String meaning, String phonetic, String wordType,
                                   String example, String synonym, String antonym, 
                                   String category, Integer difficulty, User user) {
        Vocabulary vocab = new Vocabulary();
        vocab.setWord(word);
        vocab.setMeaning(meaning);
        vocab.setPhonetic(phonetic);
        vocab.setWordType(wordType);
        vocab.setExample(example);
        vocab.setSynonym(synonym);
        vocab.setAntonym(antonym);
        vocab.setCategory(category);
        vocab.setDifficulty(difficulty);
        vocab.setUser(user);
        return vocab;
    }
}
