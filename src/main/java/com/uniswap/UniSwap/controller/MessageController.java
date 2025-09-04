package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.entity.Message;
import com.uniswap.UniSwap.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conversation/{senderId}/{receiverId}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Integer senderId, 
            @PathVariable Integer receiverId) {
        return ResponseEntity.ok(messageService.getConversation(senderId, receiverId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Message>> getMessagesByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(messageService.getMessagesByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        return ResponseEntity.ok(messageService.sendMessage(message));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Message> markAsRead(@PathVariable Integer id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }
}
