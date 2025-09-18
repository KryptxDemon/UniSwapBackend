package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.entity.Phone;
import com.uniswap.UniSwap.repository.UserRepository;
import com.uniswap.UniSwap.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PhoneRepository phoneRepository;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        System.out.println("UserService: Looking up user by email: " + email);
        Optional<User> user = userRepository.findByEmail(email);
        System.out.println("UserService: User found: " + user.isPresent());
        if (user.isPresent()) {
            System.out.println("UserService: User details - ID: " + user.get().getUserId() + ", Username: " + user.get().getUsername());
        }
        return user;
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (user.getStudentId() != null && userRepository.existsByStudentId(user.getStudentId())) {
            throw new RuntimeException("Student ID already exists");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update basic profile fields
        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }
        if (userDetails.getBio() != null) {
            user.setBio(userDetails.getBio());
        }
        if (userDetails.getProfilePicture() != null) {
            user.setProfilePicture(userDetails.getProfilePicture());
        }
        // Don't update email and student ID as they are unique identifiers
        
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public String generateUsernameSuggestion(String baseUsername) {
        String suggestion = baseUsername;
        int counter = 1;
        
        // Try adding numbers to the username until we find an available one
        while (userRepository.existsByUsername(suggestion)) {
            suggestion = baseUsername + counter;
            counter++;
            // Prevent infinite loop
            if (counter > 999) {
                suggestion = baseUsername + System.currentTimeMillis();
                break;
            }
        }
        
        return suggestion;
    }

    @Transactional
    public User createUserWithPhone(User user, String phoneNumber) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (user.getStudentId() != null && userRepository.existsByStudentId(user.getStudentId())) {
            throw new RuntimeException("Student ID already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        User savedUser = userRepository.save(user);
        
        // Create phone record
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            Phone phone = new Phone();
            phone.setUser(savedUser);
            phone.setPhoneNumber(phoneNumber);
            phoneRepository.save(phone);
        }
        
        return savedUser;
    }

    @Transactional
    public void requestAccountDeletion(Integer userId, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
        // For now, we'll just log the deletion request
        // In a real application, this would create a record in an admin review table
        System.out.println("Account deletion requested for user ID: " + userId + 
                          ", Username: " + user.getUsername() + 
                          ", Reason: " + reason);
        
        // Could also send email to admin, create admin notification, etc.
    }

    @Transactional
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
        // In a real application, you would verify the old password properly
        // This is a simplified version
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new RuntimeException("Old password is required");
        }
        
        user.setPassword(newPassword); // Note: This should be hashed in real application
        userRepository.save(user);
        
        return true;
    }
}
