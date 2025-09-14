package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Tuition;
import com.uniswap.UniSwap.entity.Post;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.repository.TuitionRepository;
import com.uniswap.UniSwap.repository.PostRepository;
import com.uniswap.UniSwap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TuitionService {

    @Autowired
    private TuitionRepository tuitionRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Tuition> getAllTuitions() {
        return tuitionRepository.findAll();
    }

    public Optional<Tuition> getTuitionById(Integer tuitionId) {
        return tuitionRepository.findById(tuitionId);
    }

    public List<Tuition> getTuitionsByUser(Integer userId) {
        return tuitionRepository.findAll().stream()
                .filter(tuition -> tuition.getPost() != null && 
                       tuition.getPost().getUser() != null && 
                       tuition.getPost().getUser().getUserId().equals(userId))
                .toList();
    }

    public List<Tuition> getTuitionsByStatus(String status) {
        return tuitionRepository.findByTStatus(status);
    }

    public List<Tuition> getTuitionsBySubject(String subject) {
        return tuitionRepository.findBySubject(subject);
    }

    public List<Tuition> getTuitionsByLocation(Integer locationId) {
        return tuitionRepository.findByLocationLocationId(locationId);
    }

    public List<Tuition> getTuitionsBySalaryRange(Integer maxSalary) {
        return tuitionRepository.findBySalaryLessThanEqual(maxSalary);
    }

    public Tuition createTuition(Tuition tuition, Integer userId) {
        // Create a new post for the tuition
        Post post = new Post();
        post.setPostTime(LocalDateTime.now());
        
        // Set the user for the post
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            post.setUser(userOpt.get());
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        // Save the post first
        post = postRepository.save(post);
        
        // Associate the post with the tuition
        tuition.setPost(post);
        
        // Set default status if not provided
        if (tuition.getTStatus() == null) {
            tuition.setTStatus("available");
        }
        
        return tuitionRepository.save(tuition);
    }

    public Tuition updateTuition(Integer tuitionId, Tuition updatedTuition) {
        Optional<Tuition> existingTuitionOpt = tuitionRepository.findById(tuitionId);
        if (existingTuitionOpt.isPresent()) {
            Tuition existingTuition = existingTuitionOpt.get();
            
            // Update fields
            if (updatedTuition.getSalary() != null) {
                existingTuition.setSalary(updatedTuition.getSalary());
            }
            if (updatedTuition.getDaysWeek() != null) {
                existingTuition.setDaysWeek(updatedTuition.getDaysWeek());
            }
            if (updatedTuition.getClazz() != null) {
                existingTuition.setClazz(updatedTuition.getClazz());
            }
            if (updatedTuition.getSubject() != null) {
                existingTuition.setSubject(updatedTuition.getSubject());
            }
            if (updatedTuition.getTStatus() != null) {
                existingTuition.setTStatus(updatedTuition.getTStatus());
            }
            if (updatedTuition.getLocation() != null) {
                existingTuition.setLocation(updatedTuition.getLocation());
            }
            
            return tuitionRepository.save(existingTuition);
        }
        throw new RuntimeException("Tuition not found with id: " + tuitionId);
    }

    public void deleteTuition(Integer tuitionId) {
        if (tuitionRepository.existsById(tuitionId)) {
            tuitionRepository.deleteById(tuitionId);
        } else {
            throw new RuntimeException("Tuition not found with id: " + tuitionId);
        }
    }

    public Tuition markAsTaken(Integer tuitionId) {
        Optional<Tuition> tuitionOpt = tuitionRepository.findById(tuitionId);
        if (tuitionOpt.isPresent()) {
            Tuition tuition = tuitionOpt.get();
            tuition.setTStatus("taken");
            return tuitionRepository.save(tuition);
        }
        throw new RuntimeException("Tuition not found with id: " + tuitionId);
    }

    public Tuition markAsCompleted(Integer tuitionId) {
        Optional<Tuition> tuitionOpt = tuitionRepository.findById(tuitionId);
        if (tuitionOpt.isPresent()) {
            Tuition tuition = tuitionOpt.get();
            tuition.setTStatus("completed");
            return tuitionRepository.save(tuition);
        }
        throw new RuntimeException("Tuition not found with id: " + tuitionId);
    }
}
