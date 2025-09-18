// src/main/java/com/uniswap/UniSwap/controller/UserController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails, Authentication auth) {
        // Check if user is authenticated
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            // Get the authenticated user
            User currentUser = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
            
            // Check if the user is updating their own profile or if they're an admin
            if (!currentUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id, Authentication auth) {
        // Check if user is authenticated
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            // Get the authenticated user
            User currentUser = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
            
            // Check if the user is deleting their own profile or if they're an admin
            if (!currentUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Integer id, @RequestBody Map<String, String> passwords, Authentication auth) {
        // Check if user is authenticated
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            // Get the authenticated user
            User currentUser = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
            
            // Check if the user is updating their own password
            if (!currentUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot change another user's password");
            }
            
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Old password and new password are required");
            }
            
            boolean success = userService.changePassword(id, oldPassword, newPassword);
            if (success) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to change password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error changing password: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/request-deletion")
    public ResponseEntity<String> requestAccountDeletion(@PathVariable Integer id, @RequestBody Map<String, String> request, Authentication auth) {
        // Check if user is authenticated
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            // Get the authenticated user
            User currentUser = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
            
            // Check if the user is requesting deletion of their own account
            if (!currentUser.getUserId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot request deletion for another user's account");
            }
            
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reason for deletion is required");
            }
            
            userService.requestAccountDeletion(id, reason);
            return ResponseEntity.ok("Account deletion request submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting deletion request: " + e.getMessage());
        }
    }
}
