package com.uniswap.UniSwap.service;

import com.uniswap.UniSwap.entity.Message;
import com.uniswap.UniSwap.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Message> getMessageById(Integer id) {
        return messageRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(Integer senderId, Integer receiverId) {
        return messageRepository.findConversationBetweenUsers(senderId, receiverId);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByUserId(Integer userId) {
        return messageRepository.findBySenderUserIdOrReceiverUserId(userId, userId);
    }

    @Transactional
    public Message sendMessage(Message message) {
        message.setSentTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public Message markAsRead(Integer id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        // Add read status field to Message entity if needed
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Integer id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        messageRepository.delete(message);
    }
}
