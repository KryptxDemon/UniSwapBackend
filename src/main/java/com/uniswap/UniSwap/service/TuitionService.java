package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Tuition;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.repository.TuitionRepository;
import com.uniswap.UniSwap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TuitionService {

    @Autowired
    private TuitionRepository tuitionRepository;
    
    @Autowired
    private UserRepository userRepository;    public List<Tuition> getAllTuitions() {
        return tuitionRepository.findAll();
    }

    public Optional<Tuition> getTuitionById(Integer tuitionId) {
        return tuitionRepository.findById(tuitionId);
    }

    public List<Tuition> getTuitionsByUser(Integer userId) {
        return tuitionRepository.findByUserUserId(userId);
    }

    public List<Tuition> getTuitionsByStatus(String status) {
        return tuitionRepository.findByTStatus(status);
    }

    public List<Tuition> getTuitionsBySubject(String subject) {
        return tuitionRepository.findBySubject(subject);
    }

    public List<Tuition> getTuitionsByLocation(String location) {
        return tuitionRepository.findByLocation(location);
    }

    public List<Tuition> getTuitionsBySalaryRange(Integer maxSalary) {
        return tuitionRepository.findBySalaryLessThanEqual(maxSalary);
    }

    public Tuition createTuition(Tuition tuition, Integer userId) {
        // Set the user relationship
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            tuition.setUser(userOpt.get());
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
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
