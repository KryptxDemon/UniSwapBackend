// src/main/java/com/uniswap/UniSwap/controller/MessageController.java
package com.uniswap.UniSwap.controller;

import com.uniswap.UniSwap.dto.MessageDTO;
import com.uniswap.UniSwap.dto.ConversationDTO;
import com.uniswap.UniSwap.entity.Message;
import com.uniswap.UniSwap.entity.User;
import com.uniswap.UniSwap.entity.Item;
import com.uniswap.UniSwap.service.MessageService;
import com.uniswap.UniSwap.service.UserService;
import com.uniswap.UniSwap.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired private MessageService messageService;
    @Autowired private UserService userService;
    @Autowired private ItemService itemService;

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(@PathVariable Integer userId) {
        try {
            List<Message> userMessages = messageService.getMessagesByUserId(userId);

            Map<Integer, List<Message>> conversationGroups = new HashMap<>();

            // Group messages by conversation partner
            for (Message message : userMessages) {
                Integer partnerId = message.getSender().getUserId().equals(userId)
                    ? message.getReceiver().getUserId()
                    : message.getSender().getUserId();

                conversationGroups.computeIfAbsent(partnerId, k -> new java.util.ArrayList<>()).add(message);
            }

            // Convert to DTOs
            List<ConversationDTO> conversations = conversationGroups.entrySet().stream()
                .map(entry -> {
                    Integer partnerId = entry.getKey();
                    List<Message> messages = entry.getValue();
                    Message lastMessage = messages.stream()
                        .max((m1, m2) -> m1.getSentTime().compareTo(m2.getSentTime()))
                        .orElse(null);

                    User partner = userService.getUserById(partnerId).orElse(null);
                    
                    ConversationDTO conversation = new ConversationDTO();
                    conversation.setPartnerId(partnerId);
                    conversation.setPartnerUsername(partner != null ? partner.getDisplayUsername() : "Unknown");
                    conversation.setPartnerEmail(partner != null ? partner.getEmail() : "");
                    conversation.setLastMessage(lastMessage != null ? lastMessage.getText() : "");
                    conversation.setLastMessageTime(lastMessage != null ? lastMessage.getSentTime() : null);
                    conversation.setMessageCount(messages.size());
                    conversation.setUnreadCount(0); // TODO: Implement read status

                    return conversation;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            System.err.println("Error getting conversations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/conversation/{senderId}/{receiverId}")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @PathVariable Integer senderId,
            @PathVariable Integer receiverId) {
        try {
            List<Message> messages = messageService.getConversation(senderId, receiverId);
            List<MessageDTO> messageDTOs = messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(messageDTOs);
        } catch (Exception e) {
            System.err.println("Error getting conversation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody Map<String, Object> messageData, Authentication auth) {
        try {
            Integer senderId = (Integer) messageData.get("senderId");
            Integer receiverId = (Integer) messageData.get("receiverId");
            String text = (String) messageData.get("text");
            Integer itemId = (Integer) messageData.get("itemId");

            // Verify authentication
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            User sender = userService.getUserById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
            User receiver = userService.getUserById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Verify the authenticated user is the sender
            if (!sender.getEmail().equals(auth.getName())) {
                return ResponseEntity.status(403).build();
            }

            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setText(text);

            if (itemId != null) {
                Item item = itemService.getItemById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found"));
                message.getItems().add(item);
            }

            Message savedMessage = messageService.sendMessage(message);
            return ResponseEntity.ok(convertToDTO(savedMessage));
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id, Authentication auth) {
        try {
            // Verify authentication
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(401).build();
            }

            Message message = messageService.getMessageById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

            // Verify the authenticated user is the sender
            if (!message.getSender().getEmail().equals(auth.getName())) {
                return ResponseEntity.status(403).build();
            }

            messageService.deleteMessage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Helper method to convert Message entity to DTO
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setText(message.getText());
        dto.setSentTime(message.getSentTime());
        
        // Convert sender
        MessageDTO.UserSummaryDTO senderDTO = new MessageDTO.UserSummaryDTO();
        senderDTO.setUserId(message.getSender().getUserId());
        senderDTO.setDisplayUsername(message.getSender().getDisplayUsername());
        senderDTO.setEmail(message.getSender().getEmail());
        dto.setSender(senderDTO);
        
        // Convert receiver
        MessageDTO.UserSummaryDTO receiverDTO = new MessageDTO.UserSummaryDTO();
        receiverDTO.setUserId(message.getReceiver().getUserId());
        receiverDTO.setDisplayUsername(message.getReceiver().getDisplayUsername());
        receiverDTO.setEmail(message.getReceiver().getEmail());
        dto.setReceiver(receiverDTO);
        
        return dto;
    }
}
