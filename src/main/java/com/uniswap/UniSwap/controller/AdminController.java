// src/main/java/com/uniswap/UniSwap/controller/AdminController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private MessageRepository messageRepository;
    @Autowired private BorrowRecordRepository borrowRecordRepository;
    @Autowired private SwapRequestRepository swapRequestRepository;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private TuitionRepository tuitionRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PhoneRepository phoneRepository;
    @Autowired private UserRepository userRepository;

    @DeleteMapping("/reset")
    @Transactional
    public ResponseEntity<Void> resetAll() {
        messageRepository.deleteAll();
        borrowRecordRepository.deleteAll();
        swapRequestRepository.deleteAll();
        wishlistRepository.deleteAll();
        itemRepository.deleteAll();
        tuitionRepository.deleteAll();
        postRepository.deleteAll();
        phoneRepository.deleteAll();
        userRepository.deleteAll();
        // locationRepository.deleteAll();
        // categoryRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}

