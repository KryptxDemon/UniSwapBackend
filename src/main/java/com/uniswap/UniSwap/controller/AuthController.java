package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.dto.AuthResponse;
import com.uniswap.UniSwap.dto.LoginRequest;
import com.uniswap.UniSwap.dto.RegisterRequest;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.security.JwtTokenUtil;
import com.uniswap.UniSwap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userService.getUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        String jwt = jwtTokenUtil.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getUserId(), user.getDisplayUsername(), user.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStudentId(registerRequest.getStudentId());

        User savedUser = userService.createUser(user);
        String jwt = jwtTokenUtil.generateToken(savedUser);

        return ResponseEntity.ok(new AuthResponse(jwt, savedUser.getUserId(), 
            savedUser.getDisplayUsername(), savedUser.getEmail()));
    }
}
