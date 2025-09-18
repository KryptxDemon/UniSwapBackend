// src/main/java/com/uniswap/UniSwap/controller/AuthController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.dto.AuthResponse;
import com.uniswap.UniSwap.dto.LoginRequest;
import com.uniswap.UniSwap.dto.RegisterRequest;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.security.JwtTokenUtil;
import com.uniswap.UniSwap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.getUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(principal);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getUserId(), user.getDisplayUsername(), user.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setStudentId(registerRequest.getStudentId());

            User savedUser = userService.createUserWithPhone(user, registerRequest.getPhone());

            // Don't auto-login after registration, just return success message
            return ResponseEntity.ok(new AuthResponse(null, savedUser.getUserId(),
                    savedUser.getDisplayUsername(), savedUser.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsernameAvailability(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        if (exists) {
            // Generate suggestions
            String suggestion = userService.generateUsernameSuggestion(username);
            return ResponseEntity.ok().body(new UsernameCheckResponse(false, suggestion));
        }
        return ResponseEntity.ok().body(new UsernameCheckResponse(true, null));
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmailAvailability(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok().body(new EmailCheckResponse(!exists));
    }

    // Helper classes for responses
    public static class UsernameCheckResponse {
        public boolean available;
        public String suggestion;

        public UsernameCheckResponse(boolean available, String suggestion) {
            this.available = available;
            this.suggestion = suggestion;
        }

        // Getters
        public boolean isAvailable() { return available; }
        public String getSuggestion() { return suggestion; }
    }

    public static class EmailCheckResponse {
        public boolean available;

        public EmailCheckResponse(boolean available) {
            this.available = available;
        }

        // Getter
        public boolean isAvailable() { return available; }
    }
}
